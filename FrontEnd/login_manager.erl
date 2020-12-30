-module(login_manager).
-export([start/0, create_account/3, close_account/2, login/2,isloggedIn/2,getDist/1,logOut/1, ativar/2, desativar/2]).
-import(maps,[update/2, remove/2]).
-import(lists,[append/2, delete/2]).

start() ->
	Pid = spawn(fun() -> loop(#{}) end),
	register(?MODULE, Pid). % ou register(?MODULE, spawn(fun() -> loop(#{}) end)).

rpc(Request) ->
	?MODULE ! {Request, self()},
	receive
		{Result, ?MODULE} -> Result
	end.


create_account(Username, Password,District) ->
	rpc({create_account, Username, Password,District,[]}).

close_account(Username, Password) ->
	rpc({close_account, Username, Password}).


isloggedIn(Username,Password) ->
	rpc({isloggedIn,Username,Password}).


logOut(Username) ->
	rpc({logOut,Username}).	

getDist(Username) ->
	rpc({getDist,Username}).


login(Username, Password) ->
	rpc({login, Username, Password}).

ativar(Username, Not) ->
	rpc({ativar, Username, Not}).

desativar(Username, Not) ->
	rpc({desativar, Username, Not}).	

loop(Accounts) ->
	receive
		{{create_account, Username, Password,District,L}, From} ->
			case maps:find(Username, Accounts) of
				error -> 
					From ! {"ok", ?MODULE},
					loop(maps:put(Username, {Password,false,District,L}, Accounts));
				_ ->
					From ! {"user_exists", ?MODULE},
					loop(Accounts)
			end;
		{{close_account, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Password, _, _}} ->
					From ! {"ok", ?MODULE},
					loop(maps:remove(Username, Accounts));
				_ ->
					From ! {"invalid", ?MODULE},
					loop(Accounts)
			end;
		{{login, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Pass, _,District,L}} -> if 
					Pass == Password -> From ! {L, ?MODULE},loop(maps:update(Username,{Password,true,District,L},Accounts));
				  	true -> From ! {"invalid_password", ?MODULE},loop(Accounts)
				end;	  
				_ ->
					From ! {"invalid_username", ?MODULE},
					loop(Accounts)
			end;
		{{logOut,Username},From} ->
			io:format("\nEntrei\n"),
			case maps:find(Username, Accounts) of
				{ok,{X,true,Y,L}} -> From ! {"ok", ?MODULE},
								 loop(maps:update(Username,{X,false,Y,L},Accounts));				 
				true -> io:format("$$$$$$$$$$")				 	
			end;		
		{{isloggedIn,Username,Password},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Pass,true,District,L}} ->   
					if Pass == Password -> 
						From ! {District, ?MODULE},loop(maps:update(Username,{Password,true,District,L},Accounts));          
					true -> 
						From ! {"invalid_password", ?MODULE} end; %%%%%%%%%%%%% erro sem o update
				{ok,{Pass,false,District,L}} ->  
					if Pass == Password -> 
						From ! {"notLogged", ?MODULE}, loop(maps:update(Username,{Password,false,District,L},Accounts)); 
					true -> 
						From ! {"invalid_password", ?MODULE} end; 
				_ ->  From ! {"invalid_password", ?MODULE}
				end;
		{{ativar,Username,Not},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Password,V,District,L}} ->
					if length(L) < 3 ->
						io:format("\n"),
						io:format("length a funcionar"),
						Upd = [Not,L],
						io:format("\n\n\n\n\n\n\n\n---------------->"),
						io:format(Upd),
						io:format("\n\n\n\n\n\n\n\n---------------->"),
						From ! {"ok", ?MODULE},
						loop(maps:update(Username,{Password,V,District,[Not|L]},Accounts));
					true ->
						From ! {"nok", ?MODULE},
						loop(Accounts)
					end
				end;
		{{desativar,Username,Not},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Password,V,District,L}} ->  
					if length(L) < 4 ->
						From ! {delete(Not,L), ?MODULE},
						loop(maps:update(Username,{Password,V,District,delete(Not,L)},Accounts));
					true ->
						From ! {L, ?MODULE},
						loop(Accounts)
					end
				end;								
		{{getDist,Username},From} ->
			case maps:find(Username,Accounts) of
				{ok,{_,_,District,L}} ->  From ! {District, ?MODULE}
				end
	end.
