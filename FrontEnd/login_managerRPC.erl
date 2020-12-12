-module(login_managerRPC).
-export([start/0, create_account/2, close_account/2, login/2]).
-import(maps,[update/2, remove/2]).

start() ->
	Pid = spawn(fun() -> loop(#{#{}}) end),
	register(?MODULE, Pid). % ou register(?MODULE, spawn(fun() -> loop(#{}) end)).

rpc(Request) ->
	?MODULE ! {Request, self()},
	receive
		{Result, ?MODULE} -> Result
	end.


create_account(Username, Password) ->
	rpc({create_account, Username, Password}).

close_account(Username, Password) ->
	rpc({close_account, Username, Password}).

login(Username, Password) ->
	rpc({login, Username, Password}).

loop(Districts) ->
	receive
		%{create_account, Username, Password, From} ->
		{{create_account, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				error -> 
					From ! {ok, ?MODULE},
					loop(maps:put(Username, {Password, false}, Accounts));
				_ ->
					From ! {user_exists, ?MODULE},
					loop(Accounts)
			end;

		%{close_account, Username, Password, From} ->
		{{close_account, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Password, _}} ->
					From ! {ok, ?MODULE},
					loop(maps:remove(Username, Accounts));
				_ ->
					From ! {invalid, ?MODULE},
					loop(Accounts)
			end;
		{{login, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Password, _}} ->
					From ! {ok, ?MODULE},
					loop(Accounts);
				_ ->
					From ! {invalid, ?MODULE},
					loop(Accounts)
			end
	end.
