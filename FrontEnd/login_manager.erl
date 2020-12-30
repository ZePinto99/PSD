-module(login_manager).
-export([start/0, create_account/4, close_account/2, login/2,isloggedIn/2,setInfetado/1,logOut/1, ativar/2, desativar/2]).
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


create_account(Username, Password,District,Infectado) ->
	rpc({create_account, Username, Password,District,[],Infectado}).

close_account(Username, Password) ->
	rpc({close_account, Username, Password}).


isloggedIn(Username,Password) ->
	rpc({isloggedIn,Username,Password}).


logOut(Username) ->
	rpc({logOut,Username}).	

setInfetado(Username) ->
	rpc({setInfetado,Username}).


login(Username, Password) ->
	rpc({login, Username, Password}).

ativar(Username, Not) ->
	rpc({ativar, Username, Not}).

desativar(Username, Not) ->
	rpc({desativar, Username, Not}).	

loop(Accounts) ->
	receive
		{{create_account, Username, Password,District,L,Infectado}, From} ->
			case maps:find(Username, Accounts) of
				error -> 
					From ! {"ok", ?MODULE},
					loop(maps:put(Username, {Password,false,District,L,Infectado}, Accounts));
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
				{ok, {Pass, _,District,L,false}} -> if 
					Pass == Password -> From ! {L, ?MODULE},loop(maps:update(Username,{Password,true,District,L,false},Accounts));
				  	true -> From ! {"invalid_password", ?MODULE},loop(Accounts)
				end;
				{ok, {Pass, _,District,L,true}} -> From ! {"Bloq", ?MODULE},
					loop(Accounts);	  
				_ ->
					From ! {"invalid_username", ?MODULE},
					loop(Accounts)
			end;
		{{logOut,Username},From} ->
			io:format("\nEntrei\n"),
			case maps:find(Username, Accounts) of
				{ok,{X,true,Y,L,Infectado}} -> From ! {"ok", ?MODULE},
								 loop(maps:update(Username,{X,false,Y,L,Infectado},Accounts));				 
				true -> From ! {"nok", ?MODULE},loop(Accounts)			 	
			end;		
		{{isloggedIn,Username,Password},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Pass,true,District,L,Infectado}} ->   
					if Pass == Password -> 
						From ! {District, ?MODULE},loop(maps:update(Username,{Password,true,District,L,Infectado},Accounts));          
					true -> 
						From ! {"invalid_password", ?MODULE} end, loop(Accounts); %%%%%%%%%%%%% erro sem o update
				{ok,{Pass,false,District,L,Infectado}} ->  
					if Pass == Password -> 
						From ! {"notLogged", ?MODULE}, loop(maps:update(Username,{Password,false,District,L,Infectado},Accounts)); 
					true -> 
						From ! {"invalid_password", ?MODULE} end, loop(Accounts); 
				_ ->  From ! {"invalid_password", ?MODULE},loop(Accounts)
				end;
		{{ativar,Username,Not},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Password,V,District,L,Infectado}} ->
					if length(L) < 3 ->
						io:format("\n"),
						io:format("length a funcionar"),
						Upd = [Not,L],
						io:format("\n\n\n\n\n\n\n\n---------------->"),
						io:format(Upd),
						io:format("\n\n\n\n\n\n\n\n---------------->"),
						From ! {"ok", ?MODULE},
						loop(maps:update(Username,{Password,V,District,[Not|L],Infectado},Accounts));
					true ->
						From ! {"nok", ?MODULE},
						loop(Accounts)
					end
				end;
		{{desativar,Username,Not},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Password,V,District,L,Infectado}} ->  
					if length(L) < 4 ->
						From ! {delete(Not,L), ?MODULE},
						loop(maps:update(Username,{Password,V,District,delete(Not,L),Infectado},Accounts));
					true ->
						From ! {L, ?MODULE},
						loop(Accounts)
					end
				end;								
		{{setInfetado,Username},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Password,V,District,L,Infectado}} ->   From ! {"ok", ?MODULE},loop(maps:update(Username,{Password,V,District,L,true},Accounts))
				end
	end.
