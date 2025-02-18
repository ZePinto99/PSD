-module(zmqServer).
-import(login_manager,[start/0, create_account/4, close_account/2, login/2,setInfetado/1]).

-export([main/0,publisher/0]).

main() ->
	Publisher = publisher(),
	%iniciar o socket reply que responde ao cliente
    application:ensure_started(chumak),
    {ok, SvSocket} = chumak:socket(router),
    {ok, _BindPid} = chumak:bind(SvSocket, tcp, "localhost", 12345),
    login_manager:start(),
    Distritos = ["Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre"],
    Dist = connectDistrict(12346,Distritos,#{}),
    loop(SvSocket,Dist,Publisher).

loop(SvSocket, Distritos,Publisher) ->
	%recebe um pedido registo/login
    {ok, [Identity, <<>>, Reply]} = chumak:recv_multipart(SvSocket),
    Lista = string:split(Reply,",",all),
    myForEach(Lista),

    %Vai fazer o registo/login com as funções do login_manager
    responde_usr(Lista, self(), Distritos,Publisher),

    receive
        {{_,Result,_}, ?MODULE} -> io:format("received ~p~n", [Result]),
        chumak:send_multipart(SvSocket, [Identity, <<>>, list_to_binary(Result)])    
    end,


    loop(SvSocket,Distritos,Publisher).


myForEach([])-> ok;
myForEach([H|T]) -> io:format("Question2: ~p\n", [H]),myForEach(T).

myFirst([]) -> {empty,[]};
myFirst([H|T]) -> {H,T}. 

responde_usr(Lista,From, Distritos,Publisher) ->
    {Tipo,Info} = myFirst(Lista),
    Login = <<"login">>,
    Registar = <<"registar">>,
    if
        Tipo == Login ->
            {Username,PassT} = myFirst(Info),
            {Pass,_} = myFirst(PassT),
            Resposta = login_manager:login(Username,Pass),
            if 
                Resposta == "invalid_password" -> From ! {{Tipo,"invalid_password",Username}, ?MODULE};
                Resposta == "invalid_username" -> From ! {{Tipo,"invalid_username",Username}, ?MODULE};
                Resposta == "Bloq" -> From ! {{Tipo,"User Bloqueado. Mantenha as normas da DGS e continue em isolamento.",Username}, ?MODULE};
                true -> From ! {{Tipo,listtostring(Resposta),Username}, ?MODULE}
            end;
        Tipo == Registar ->
            {Username,PassT} = myFirst(Info),
            {Password,DistrictT} = myFirst(PassT),
            {District,_} = myFirst(DistrictT),
            io:format([Username,Password,District]),
            Resposta = login_manager:create_account(Username,Password,District,false),
            From ! {{Tipo,Resposta,Username}, ?MODULE};
        true ->
            {Username,Argswithpass} = myFirst(Info),
            {Pass,Args} = myFirst(Argswithpass),
            Loggedin = login_manager:isloggedIn(Username,Pass),
            io:format("\n"),
            io:format(Loggedin),
            if
                Loggedin == "notLogged" ->
                    From ! {{Tipo,"Not logged in",Username}, ?MODULE},
                    io:format("formato desconhecido~n", []);
                Loggedin == "invalid_password" ->
                    From ! {{Tipo,"Not logged in",Username}, ?MODULE},
                    io:format("formato desconhecido~n", []);    
                true ->
                    DvS = maps:get(binary:bin_to_list(Loggedin),Distritos),
                    From ! {{Tipo,menu(DvS,Username,Args,Tipo,Publisher,Loggedin),Username}, ?MODULE}
            end
    end.


connectDistrict(X,[Distrito|Next],Distritos) ->
    if
        X<12365 ->
            {ok, DvSocket} = chumak:socket(req, integer_to_list(X)),
            {ok, _BindPid} = chumak:connect(DvSocket, tcp, "localhost", X),
            connectDistrict(X+1,Next,maps:put(Distrito,DvSocket,Distritos));
        true ->
            {ok, DvSocket} = chumak:socket(req, integer_to_list(X)),
            {ok, _BindPid} = chumak:connect(DvSocket, tcp, "localhost", X),
            maps:put(Distrito,DvSocket,maps:put(Distrito,DvSocket,Distritos))
    end.

%vai ter de receber username/id
menu(DvSocket, Username,Info,Option,Publisher,Distrito) ->
    
	%recebe a opção selecionada pelo cliente    
    
	case Option of
		<<"quit">> ->
            login_manager:logOut(Username);
		<<"localizacao">> ->
            {X,Y} = myFirst(Info),
            Tosend = ["localizacao,",Username,",",X,",",Y], 
            io:format(Tosend),
			chumak:send(DvSocket,Tosend),
			{ok,DistRep} = chumak:recv(DvSocket),
            Lista = string:split(DistRep,",",all),
            {Word,Tail} = myFirst(Lista),
            if
                Word == <<"vazia">> ->
                    {OX,OY}= myFirst(Tail),
                    Stringtosend = ["notificacao publica: Saiu uma pessoa da posicao -> (", OX, ",", OY , ") e esta ficou vazia.   Entrou uma pessoa na posicao (", X,",",Y,")"],
                    sendNotificationDistrito(Publisher,Distrito, Stringtosend),
                    "ok";
                Word == <<"saiu">>  ->
                    {OX,OY}= myFirst(Tail),
                    Stringtosend = ["notificacao publica: Saiu uma pessoa da posicao -> (", OX, ",", OY , ").   Entrou uma pessoa na posicao (", X,",",Y,")"],
                    sendNotificationDistrito(Publisher,Distrito,Stringtosend ),
                    "ok";
                Word == <<"ficou">> ->
                    "Ja se encontrava nessa posicao";
                Word == <<"entrou">> ->
                    Stringtosend = ["Entrou uma pessoa na posicao (", X,",",Y,")"],
                    sendNotificationDistrito(Publisher,Distrito,Stringtosend ),
                    "ok";
                true -> 
                "NOK"
            end;
            %sendNotificationDistrito(),
			
        <<"infoLocalizacao">> ->
			io:format(" infoLocalizacao"),
			{X,Y} = myFirst(Info),
            Tosend = ["infoLocalizacao,",Username,",",X,",",Y],
			chumak:send(DvSocket,Tosend),
			{ok, Req} = chumak:recv(DvSocket),
            io:format("\n"),
            io:format(Req),
            io:format("\n"),
			io:format("Recebi confirmação servidor"),
			binary:bin_to_list(Req);
		<<"infetado">> ->
			io:format("infetado"),
            login_manager:setInfetado(Username),
            Tosend = ["infetado,",Username], 
			chumak:send(DvSocket,Tosend),
			{ok, Req} = chumak:recv(DvSocket),
			Lista = string:split(Req,",",all),
			sendNotificationInfetado(Publisher,Lista),
			%chumak:send(DvSocket,""),
			%{ok, Req} = chumak:recv(DvSocket),
		    sendNotificationDistrito(Publisher,Distrito, "ATENCAO! Novo infetado no distrito"),
			"É Obrigatorio realizar isolamento completo por um periodo minimo de 6 anos.";
		<<"ativar">> ->
			io:format("ativar notificacoes"),
            Resposta = login_manager:ativar(Username, Info),
            io:format(Resposta),
			io:format("\n"),
            Resposta;
		<<"desativar">> ->
			io:format("desativar notificacoes"),
            Resposta = login_manager:desativar(Username, Info),
            io:format("desativar notificacoes2.0"),
            listtostring(Resposta)
	end.

publisher() ->
    {ok, Socket} = chumak:socket(pub),
    case chumak:bind(Socket, tcp, "localhost", 9999) of
        {ok, _BindPid} ->
            io:format("Binding OK with Pid: ~p\n", [Socket]);
        {error, Reason} ->
            io:format("Connection Failed for this reason: ~p\n", [Reason]);
        X ->
            io:format("Unhandled reply for bind ~p \n", [X])
    end,
    Socket.


sendNotificationDistrito(Socket,Distrito, String) ->
	ToSend = [Distrito,",",String],
    ok = chumak:send(Socket, ToSend),
    io:format(".").


sendNotificationInfetado(Socket, [H]) ->
	ToSend = ["?",H,"?,ATENCAO! Esteve em contacto com um infetado."],
	io:format(ToSend),
    ok = chumak:send(Socket,  ToSend),
    io:format(".");
sendNotificationInfetado(Socket, [H|T]) ->
	ToSend = ["?",H,"?,ATENCAO! Esteve em contacto com um infetado."],
	io:format(ToSend),
    ok = chumak:send(Socket,  ToSend),
    io:format("."),
    sendNotificationInfetado(Socket, T).


listtostring([]) -> "end,end";
listtostring([A|T]) -> io:format("\n\n\nCASO 1\n\n\n"),Str = [A,",end"],
listtostring(T,Str).


listtostring([H|T],A) ->io:format("\n\n\nCASO 2\n\n\n"),
    if
        H == "" -> 
            A;
        true -> 
            Str = [H,"," , A],
            listtostring(T,Str)
    end;        
listtostring([],A) -> io:format("\n\n\nCASO 3\n\n\n"),A.

    %% This Source Code Form is subject to the terms of the Mozilla Public
%% License, v. 2.0. If a copy of the MPL was not distributed with this
%% file, You can obtain one at http://mozilla.org/MPL/2.0/.


%%subs() ->
%%   {ok, Socket} = chumak:socket(sub),
%%    chumak:connect(Socket, tcp, "localhost", 9999),
%%%%    loop2(Socket).

%%loop2(Socket) ->
  %%  io:format("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"),
    %%{ok, Data1} = chumak:recv_multipart(Socket),
    %%io:format("Received by multipart ~p\n", [Data1]),
    %io:format("??????????????????????????????????????????????????????????????????????????????"),
    %{ok, Data2} = chumak:recv(Socket),
    %io:format("Received ~p\n", [Data2]),
    %io:format("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
    %loop2(Socket).
