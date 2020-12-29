import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Subscriber {
    public static void main(String[] dab) {
        String[] args = {"8888"};
        try (ZContext context = new ZContext();
             ZMQ.Socket socket = context.createSocket(SocketType.SUB))
        {
            socket.connect("tcp://localhost:" + args[0]);
            if (args.length == 1)
                socket.subscribe("arroz".getBytes());
            else for (int i = 1; i < args.length; i++)
                socket.subscribe(args[i].getBytes());
            while (true) {
                byte[] msg = socket.recv();
                System.out.println(new String(msg));
            }
        }
    }
}

