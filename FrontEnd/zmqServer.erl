-module(zmqServer).
-import(login_manager,[start/0, create_account/3, close_account/2, login/2]).

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
    io:format("\n"),
    io:format(Reply),
    io:format("\n"),
    Lista = string:split(Reply,",",all),
    myForEach(Lista),
    io:format("Before respond_usr\n"),
    %Vai fazer o registo/login com as funções do login_manager
    responde_usr(Lista, self(), SvSocket,Identity,Distritos,Publisher),
    io:format("After respond_usr\n"),
    receive
        {{Type,Result,Username}, ?MODULE} -> io:format("received ~p~n", [Result]),
        chumak:send_multipart(SvSocket, [Identity, <<>>, list_to_binary(Result)])    
    end,


    loop(SvSocket,Distritos,Publisher).


myForEach([])-> ok;
myForEach([H|T]) -> io:format("Question2: ~p\n", [H]),myForEach(T).

myFirst([]) -> {empty,[]};
myFirst([H|T]) -> {H,T}. 

responde_usr(Lista,From, SvSocket,Identity, Distritos,Publisher) ->
    {Tipo,Info} = myFirst(Lista),
    Login = <<"login">>,
    Registar = <<"registar">>,
    if
        Tipo == Login ->
            io:format("Entrei login\n"),
            {Username,PassT} = myFirst(Info),
            {Pass,_} = myFirst(PassT),
            Resposta = login_manager:login(Username,Pass),
            From ! {{Tipo,Resposta,Username}, ?MODULE};
        Tipo == Registar ->
            io:format("Entrei registar\n"),
            {Username,PassT} = myFirst(Info),
            {Password,DistrictT} = myFirst(PassT),
            {District,_} = myFirst(DistrictT),
            io:format([Username,Password,District]),
            Resposta = login_manager:create_account(Username,Password,District),
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
                true ->
                    DvS = maps:get(binary:bin_to_list(Loggedin),Distritos),
                    From ! {{Tipo,menu(SvSocket,DvS,Identity,Username,Args,Tipo,Publisher,Loggedin),Username}, ?MODULE}
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
menu(SvSocket, DvSocket,Identity, Username,Info,Option,Publisher,Distrito) ->
%System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
	io:format("\nMain menu\n"),
    
	%recebe a opção selecionada pelo cliente
    
    
	case Option of
		<<"quit">> ->
			io:format("cliente quer sair"),
            login_manager:logOut(Username);
		<<"localizacao">> ->
			io:format("localizacao\n"),
            {X,Y} = myFirst(Info),
            Tosend = ["localizacao,",Username,",",X,",",Y], 
            io:format(Tosend),
			chumak:send(DvSocket,Tosend),
			DistRep = chumak:recv(DvSocket),
			"ok";
        <<"infoLocalizacao">> ->
			io:format("infoLocalizacao"),
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
            Tosend = ["infetado,",Username], 
			chumak:send(DvSocket,Tosend),
			{ok, Req} = chumak:recv(DvSocket),
			Lista = string:split(Req,",",all),
			sendNotificationInfetado(Publisher,Lista),
			%chumak:send(DvSocket,""),
			%{ok, Req} = chumak:recv(DvSocket),
			%sendNotificationDistrito(Publisher,binary:bin_to_list(Req),Distrito),
			"É Obrigatorio realizar isolamento completo por um periodo minimo de 6 anos.";
		<<"ativar">> ->
			io:format("ativar notificacoes"),
			chumak:send({"ativar"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send_multipart(SvSocket,[Identity, <<>>,<<"Notificações ativadas">>]);
		<<"desativar">> ->
			io:format("desativar notificacoes"),
			chumak:send({"desativar"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send_multipart(SvSocket,[Identity, <<>>,<<"Notificações desativadas">>])
	end.

%percorre a lsita até encontrar o distrito do utilizador (vai incrementando o socket)
while(_, [], _)  -> 12346;
while(D,[D|_],Def) -> Def;
while(D,[_|T],Def) -> Ed = Def + 1, while(D, T, Ed).



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


sendNotificationDistrito(Socket, [H],Distrito) ->
	ToSend = ["?",H,"?,ATENCAO! Novo infetado no distrito "],
    ok = chumak:send(Socket,  H),
    io:format(".");
sendNotificationDistrito(Socket, [H|T],Distrito) ->
	ToSend = ["?",H,"?,ATENCAO! Novo infetado no distrito "],
    ok = chumak:send(Socket,  H),
    io:format("."),
    sendNotificationDistrito(Socket, T,Distrito).


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
