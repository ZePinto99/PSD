-module(zmqServer).
-import(login_manager,[start/0, create_account/3, close_account/2, login/2]).

-export([main/0]).

main() ->
	%iniciar o socket reply que responde ao cliente
    application:start(chumak),
    {ok, SvSocket} = chumak:socket(rep, "hello world server"),
    {ok, _BindPid} = chumak:bind(SvSocket, tcp, "localhost", 12345),
    login_manager:start(),
    %iniciar loop
    loop(SvSocket).

loop(SvSocket) ->
	%recebe um pedido registo/login
    {ok,Reply} = chumak:recv(SvSocket),

    Lista = string:split([Reply],",",all),
    myForEach(Lista),

    %Vai fazer o registo/login com as funções do login_manager
    responde_usr(Lista, self()),
    receive
        {{Type,Result}, ?MODULE} -> io:format("received ~p~n", [Result]),
        chumak:send(SvSocket, [Result])        
    end,

    io:format(Type),

    %vê se o utilizador se autenticou com sucesso (!!!Não funfa!!!)
    if Type == <<"login">> ->
       		io:format("\n"),
       		io:format("##########"),
  	     	io:format(Result),
        	if
        		Result == "ok" ->
        			%vai buscar o socket do distrito
        			io:format("Entrou ok 2"),
        			connectDistrict(Lista),
        			receive
       				{DvSocket, ?MODULE} -> menu(SvSocket, DvSocket)    %abre o menu   
   					end,
        			loop(SvSocket);
        		Result == "invalid_password" ->
        			io:format("Entrou invalid"),
        			loop(SvSocket);
        		true ->	
        			io:format(Result),
        			io:format("Entrou true"),
        			loop(SvSocket)
        	end;
        Type == <<"registar">> ->
        	loop(SvSocket)
   	end.


myForEach([])-> ok;
myForEach([H|T]) -> io:format("Question2: ~p\n", [H]),myForEach(T).

myFirst([]) -> {empty,[]};
myFirst([H|T]) -> {H,T}. 

responde_usr(Lista,From) ->
    {Tipo,Info} = myFirst(Lista),
    Login = <<"login">>,
    Registar = <<"registar">>,
    if
        Tipo == Login ->
            {Username,PassT} = myFirst(Info),
            {Pass,_} = myFirst(PassT),
            Resposta = login_manager:login(Username,Pass),
            From ! {{Tipo,Resposta}, ?MODULE};
        Tipo == Registar ->
            {Username,PassT} = myFirst(Info),
            {Password,DistrictT} = myFirst(PassT),
            {District,_} = myFirst(DistrictT),
            Resposta = login_manager:create_account(Username,Password,District),
            From ! {{Tipo,Resposta}, ?MODULE};
        false ->
            From ! {"invalid", ?MODULE},
            io:format("formato desconhecido~n", [])
    end.


connectDistrict(InfoClient) ->
    %vai buscar o socket do distrito do cliente
    Sport = getDistrict(InfoClient),

    %ligar socket requester que vai fazer pedidos ao servidor distrital
    {ok, DvSocket} = chumak:socket(req, "hello world server"),
    {ok, _BindPid} = chumak:bind(DvSocket, tcp, "localhost", Sport),

    io:format("Socket distrito feito"),
    %retorna o socket do servidor
    {DvSocket, ?MODULE}
	.

menu(SvSocket, DvSocket) ->
%System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
	io:format("Hello menu"),
	%recebe a opção selecionada pelo cliente
    {ok,Req} = chumak:recv(SvSocket),
    {Option,Info} = string:split([Req],",",all),
    myForEach(Option),
	case Option of
		<<"quit">> ->
			io:format("cliente quer sair");
			%islogout
		<<"localizacao">> ->
			io:format("localizacao"),
			{x,y} = string:split([Info],",",all),
			chumak:send(["localizacao",{"x","y"}]),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send("Localização atualizada"),
			menu(SvSocket, DvSocket);
		<<"infoLocalizacao">> ->
			io:format("infoLocalizacao"),
			{x,y} = string:split([Info],",",all),
			chumak:send(["infoLocalizacao",{"x","y"}]),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send("Info da Localização"),
			menu(SvSocket, DvSocket);
		<<"infetado">> ->
			io:format("infetado"),
			chumak:send({"infetado"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send("Estado mudado para infetado"),
			menu(SvSocket, DvSocket);
		<<"ativar">> ->
			io:format("ativar notificacoes"),
			chumak:send({"ativar"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send("Notificações ativadas"),
			menu(SvSocket, DvSocket);
		<<"desativar">> ->
			io:format("desativar notificacoes"),
			chumak:send({"desativar"}),
			{ok, Req} = chumak:recv(DvSocket),
			io:format("Recebi confirmação servidor"),
			chumak:send("Notificações desativadas"),
			menu(SvSocket, DvSocket)
	end.

getDistrict(Lista) ->
	io:format("\nget district"),
	%vou buscar o distrito do cliente
	{_,Info} = myFirst(Lista),
	{Username,_} = myFirst(Info),
	Distrito = login_manager:getDist(Username),

	%todos os distritos existentes (ordem com a mesma lsita do servidor distrital)
	Distritos = {"Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarém", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Bragança", "Portalegre"},
	%envia o socket do distrito do cliente
	{while(Distrito, Distritos, 5555), ?MODULE}.

%percorre a lsita até encontrar o distrito do utilizador (vai incrementando o socket)
while(_, [], _)  -> 5555;
while(D,[D|_],Def) -> Def;
while(D,[_|T],Def) -> Def = Def + 1, while(D, T, Def).
