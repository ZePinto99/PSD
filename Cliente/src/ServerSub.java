import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashSet;
import java.util.Set;

public class ServerSub {

    public static void main(String[] args) throws Exception {
        if(args.length < 2){
            System.out.println("Invalid argument amount.");
            return;
        }
        ZContext context = new ZContext();

        ZMQ.Socket receiver = context.createSocket(SocketType.SUB);
        receiver.bind("tcp://*:" + args[0]);
        receiver.subscribe("".getBytes());
        System.out.println("Receiving from clients at port " + args[0] + ".");

        ZMQ.Socket sender = context.createSocket(SocketType.PUB);
        for(int i=1; i<args.length; i++) {
            sender.connect("tcp://*:" + args[i]);
            System.out.println("Connecting to publisher server " + args[i] + ".");
        }

        while (true) {
            byte[] msg = receiver.recv();
            System.out.println("recv [" + "data.roomString" + "] ");
            sender.send(msg);
        }

    }
}