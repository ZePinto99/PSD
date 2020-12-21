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

    responde_usr(Lista),

    chumak:send(SvSocket, <<"Hello Friend">>),
    loop(SvSocket).


myForEach([])-> ok;
myForEach([H|T]) -> io:format("Question2: ~p\n", [H]),myForEach(T).

myFirst([]) -> {empty,[]};
myFirst([H|T]) -> {H,T}. 

responde_usr(Lista) ->
    {Tipo,Info} = myFirst(Lista),
    Login = <<"login">>,
    Registar = <<"registar">>,
    case Tipo == Login of
        true -> 
            {Username,PassT} = myFirst(Info),
            {Pass,_} = myFirst(PassT),
            Resposta = login_manager:login(Username,Pass),
            io:format("received ~p~n", [Resposta]);
        false -> 
            case Tipo == Registar of
                true -> 
                    {Username,PassT} = myFirst(Info),
                    {Password,DistrictT} = myFirst(PassT),
                    {District,_} = myFirst(DistrictT),
                    Resposta = login_manager:create_account(Username,Password,District),
                    io:format("received ~p~n", [Resposta]);
                false -> io:format("formato desconhecido~n", [])
            end
    end.


