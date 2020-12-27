-module(zmqServer).
-import(login_manager,[start/0, create_account/3, close_account/2, login/2]).

-export([main/0]).

main() ->
	%iniciar o socket reply que responde ao cliente
    application:ensure_started(chumak),
    {ok, SvSocket} = chumak:socket(router),
    {ok, _BindPid} = chumak:bind(SvSocket, tcp, "localhost", 12345),
    login_manager:start(),
    DvSocket = connectDistrict(self()),
    %iniciar loop (!!!!!!!!!!!!!!!!Falta iniciar uma lista com os sockets distritais abertos!!!!!!!!!!!!!!!!)
    loop(SvSocket,DvSocket).

loop(SvSocket,DvSocket) ->
	%recebe um pedido registo/login
    {ok, [Identity, <<>>, Reply]} = chumak:recv_multipart(SvSocket),
    io:format("\n"),
    io:format(Reply),
    io:format("\n"),
    Lista = string:split(Reply,",",all),
    myForEach(Lista),
    io:format("Before respond_usr\n"),
    %Vai fazer o registo/login com as funções do login_manager
    responde_usr(Lista, self(), SvSocket, DvSocket,Identity),
    io:format("After respond_usr\n"),
    receive
        {{Type,Result,Username}, ?MODULE} -> io:format("received ~p~n", [Result]),
        chumak:send_multipart(SvSocket, [Identity, <<>>, list_to_binary(Result)])    
    end,


    loop(SvSocket,DvSocket).


myForEach([])-> ok;
myForEach([H|T]) -> io:format("Question2: ~p\n", [H]),myForEach(T).

myFirst([]) -> {empty,[]};
myFirst([H|T]) -> {H,T}. 

responde_usr(Lista,From, SvSocket, DvSocket,Identity) ->
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
            {Username,Args} = myFirst(Info),
            Loggedin = login_manager:isloggedIn(Username),
            if
                 Loggedin == "ok" ->
                    From ! {{Tipo,menu(SvSocket,DvSocket,Identity,Username,Args,Tipo),Username}, ?MODULE};
                true ->
                    From ! {{Tipo,"Not logged in",Username}, ?MODULE},
                    io:format("formato desconhecido~n", [])
            end
    end.


connectDistrict( From) ->
    %vai buscar o socket do distrito do cliente
    %Sport = getDistrict(InfoClient, From),

    %ligar socket requester que vai fazer pedidos ao servidor distrital
    {ok, DvSocket} = chumak:socket(req, "hello district server"),
    {ok, _BindPid} = chumak:connect(DvSocket, tcp, "localhost", 12346),

    %chumak:send(DvSocket, ["HELLO"]),
    %io:format(chumak:recv(DvSocket)),

    %retorna o socket do servidor
    DvSocket
	.

%vai ter de receber username/id
menu(SvSocket, DvSocket,Identity, Username,Info,Option) ->
%System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
	io:format("\nMain menu\n"),
    
	%recebe a opção selecionada pelo cliente
    
    
	case Option of
		<<"quit">> ->
			io:format("cliente quer sair");
			%islogout
		<<"localizacao">> ->
			io:format("localizacao\n"),
            io:format(Info),
            {X,Y} = myFirst(Info),
            Tosend = ["localizacao,",Username,",",X,",",Y], 
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
			chumak:send({"infetado"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send_multipart(SvSocket,[Identity, <<>>,<<"Estado mudado para infetado">>]);
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

getDistrict(Lista, From) ->
	io:format("\nget district"),
	%vou buscar o distrito do cliente
	{_,Info} = myFirst(Lista),
	{Username,_} = myFirst(Info),
	Distrito = login_manager:getDist(Username),

	%todos os distritos existentes (ordem com a mesma lsita do servidor distrital)
	Distritos = ["Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre"],
	%envia o socket do distrito do cliente
    Result = while(Distrito, Distritos, 12346),
    %io:format("\n\n" + Result + "\n\n"),
	From ! {Result, ?MODULE},
    Result.

%percorre a lsita até encontrar o distrito do utilizador (vai incrementando o socket)
while(_, [], _)  -> 12346;
while(D,[D|_],Def) -> Def;
while(D,[_|T],Def) -> Ed = Def + 1, while(D, T, Ed).
