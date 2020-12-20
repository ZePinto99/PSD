
.PHONY: *.erl

RUN_ARGS = erl -pa ../../chumak/_build/default/lib/chumak/ebin -eval

*.erl:
	erlc $@


zmqServer: zmqServer.erl login_manager.erl
	${RUN_ARGS} "zmqServer:main()"



