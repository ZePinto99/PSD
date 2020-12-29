package Client;

import org.zeromq.ZContext;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import static java.lang.Thread.sleep;

public class Publisher {
    public static void main(String[] dab) {
        String[] args = {"8888"};
        try (ZContext context = new ZContext();
             ZMQ.Socket socket = context.createSocket(SocketType.PUB))
        {
            socket.bind("tcp://*:" + args[0]);
            while (true) {
                String str = "Ola";
                if (str == null) break;
                socket.send("arroz qualquer coisa");
            }
        }
    }
}