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
    {Cabeca,Cauda} = myFirst(Lista),
    {Cabeca2,Cauda2} = myFirst(Cauda),
    {Cabeca3,Cauda3} = myFirst(Cauda2),
    {Cabeca4,Cauda4} = myFirst(Cauda3),
    Login = <<"login">>,
    Registar = <<"registar">>,
    case Cabeca == Login of
        true -> Resposta = login_manager:login(Cabeca2,Cabeca3),io:format("received ~p~n", [Resposta]);
        false -> case Cabeca == Registar of
            true -> Resposta = login_manager:create_account(Cabeca2,Cabeca3,Cabeca4),io:format("received ~p~n", [Resposta]);
            false -> io:format("formato desconhecido~n", [])
        end
    end.


