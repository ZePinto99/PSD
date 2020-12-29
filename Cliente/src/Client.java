import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Client {

    static Random random = new Random();
    static String room = "main";

    static String getRoom(){
        synchronized (room){
            return room;
        }
    }

    static void changeRoom(ZMQ.Socket sock, String newRoom){
        synchronized (room){
            System.out.println("Changing room to " + newRoom);

            sock.unsubscribe(Data.roomCodeFrom(room));
            sock.subscribe(Data.roomCodeFrom(newRoom));
            room = newRoom;
        }
    }


    public static void main(String[] args) throws Exception {
        int numSub = Integer.parseInt(args[0]);
        int numPub = Integer.parseInt(args[1]);
        String sendPort = args[2 + random.nextInt(numSub)];
        String recvPort = args[2 + numSub + random.nextInt(numPub)];

        ZContext context = new ZContext();

        ZMQ.Socket receiver = context.createSocket(SocketType.SUB);
        receiver.connect("tcp://*:" + recvPort);
        changeRoom(receiver, room);
        System.out.println("Connecting to publisher server at port " + recvPort + ".");

        ZMQ.Socket sender = context.createSocket(SocketType.PUB);
        sender.connect("tcp://*:" + sendPort);
        System.out.println("Connecting to subscriber server at port " + sendPort + ".");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println(new String(receiver.recv(), StandardCharsets.UTF_8));
                    }
                } catch (Exception e){}
            }
        }).start();

        while (true){
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if(str.matches("^\\\\room ([a-zA-Z_0-9])+\n?")) {
                changeRoom(receiver, str.substring(6));
            } else {
                Data data = new Data(getRoom(), str);
                sender.send(data.encode());
            }
        }
    }
}