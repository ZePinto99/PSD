-module(zmqServer).
-import(login_manager,[start/0, create_account/3, close_account/2, login/2]).

-export([main/0]).

main() ->
    application:start(chumak),
    {ok, SvSocket} = chumak:socket(rep, "hello world server"),
    {ok, _BindPid} = chumak:bind(SvSocket, tcp, "localhost", 12345),
    login_manager:start(),
    loop(SvSocket).

loop(SvSocket) ->
    {ok,Reply} = chumak:recv(SvSocket),

    Lista = string:split([Reply],",",all),
    myForEach(Lista),

    responde_usr(Lista, self()),
    receive
        {Result, ?MODULE} -> io:format("received ~p~n", [Result]),
        chumak:send(SvSocket, [Result])        
    end,

    loop(SvSocket).


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
            From ! {Resposta, ?MODULE},
            io:format("received ~p~n", [Resposta]);
        Tipo == Registar ->
            {Username,PassT} = myFirst(Info),
            {Password,DistrictT} = myFirst(PassT),
            {District,_} = myFirst(DistrictT),
            Resposta = login_manager:create_account(Username,Password,District),
            From ! {Resposta, ?MODULE},
            io:format("received ~p~n", [Resposta]);
        false ->
            From ! {"invalid", ?MODULE},
            io:format("formato desconhecido~n", [])
    end.


