-module(login_manager).
-export([start/0, create_account/3, close_account/2, login/2,isloggedIn/2,getDist/1,logOut/1]).
-import(maps,[update/2, remove/2]).

start() ->
	Pid = spawn(fun() -> loop(#{}) end),
	register(?MODULE, Pid). % ou register(?MODULE, spawn(fun() -> loop(#{}) end)).

rpc(Request) ->
	?MODULE ! {Request, self()},
	receive
		{Result, ?MODULE} -> Result
	end.


create_account(Username, Password,District) ->
	rpc({create_account, Username, Password,District}).

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

loop(Accounts) ->
	receive
		%{create_account, Username, Password, From} ->
		{{create_account, Username, Password,District}, From} ->
			case maps:find(Username, Accounts) of
				error -> 
					From ! {"ok", ?MODULE},
					loop(maps:put(Username, {Password,false,District}, Accounts));
				_ ->
					From ! {"user_exists", ?MODULE},
					loop(Accounts)
			end;
		{{close_account, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Password, _}} ->
					From ! {"ok", ?MODULE},
					loop(maps:remove(Username, Accounts));
				_ ->
					From ! {"invalid", ?MODULE},
					loop(Accounts)
			end;
		{{login, Username, Password}, From} ->
			case maps:find(Username, Accounts) of
				{ok, {Pass, _,District}} -> if 
					Pass == Password -> From ! {"ok", ?MODULE},loop(maps:update(Username,{Password,true,District},Accounts));
				  	true -> From ! {"invalid_password", ?MODULE},loop(Accounts)
				end;	  
				_ ->
					From ! {"invalid_username", ?MODULE},
					loop(Accounts)
			end;
		{{logOut,Username},From} ->
			io:format("\nEntrei\n"),
			case maps:find(Username, Accounts) of
				{ok,{X,true,Y}} -> From ! {"ok", ?MODULE},
								 loop(maps:update(Username,{X,false,Y},Accounts));				 
				true -> io:format("$$$$$$$$$$")				 	
			end;		
		{{isloggedIn,Username,Password},From} ->
			case maps:find(Username,Accounts) of
				{ok,{Pass,true,District}} ->   
					if Pass == Password -> 
						From ! {District, ?MODULE},loop(maps:update(Username,{Password,true,District},Accounts));          
					true -> 
						From ! {"invalid_password", ?MODULE} end; %%%%%%%%%%%%% erro sem o update
				{ok,{Pass,false,District}} ->  
					if Pass == Password -> 
						From ! {"notLogged", ?MODULE}, loop(maps:update(Username,{Password,false,District},Accounts)); 
					true -> 
						From ! {"invalid_password", ?MODULE} end; 
				_ ->  From ! {"invalid_password", ?MODULE}
				end;			
		{{getDist,Username},From} ->
			case maps:find(Username,Accounts) of
				{ok,{_,_,District}} ->  From ! {District, ?MODULE}
				end	
	end.
