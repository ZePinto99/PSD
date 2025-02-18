package Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.pattern.SyslogStartConverter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import resources.Distrito;


public class DistrictService {
    private static DistrictService inst;
    private final int portDefault = 12376;
    private List<ZMQ.Socket> socketList;
    private ZContext context;

    private DistrictService(){
        System.out.println("inicio");

        socketList = new ArrayList<>();

        context = new ZContext();
            //  Socket to send messages on
        for(int i = 0; i < Distrito.values().length;i++){

                int port = portDefault + i;
                ZMQ.Socket requester = context.createSocket(SocketType.REQ);
                String processName =
                        java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
                long pid =  Long.parseLong(processName.split("@")[0]);
                requester.setIdentity(String.valueOf(pid).getBytes());
                requester.connect("tcp://127.0.0.1:" + port);


                socketList.add(requester);
            }



    }

    public static DistrictService getInstance()
    {
        if (inst == null)
            inst = new DistrictService();

        return inst;
    }


    public int getNumberOfUsersInDistrict(String distrito) {
        String resposta = null;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getNumUsers";

        System.out.println("mandei msg");
        try {
            socket.send(str.getBytes(ZMQ.CHARSET),0);
            resposta =new String(socket.recv(), StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }


        System.out.println("recebi msg");


        return Integer.parseInt(resposta);

    }


    public int getNumberOfInfectedInDistrict(String distrito){
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getNumInfected";

        socket.send(str);

        resposta = socket.recvStr();

        return Integer.parseInt(resposta);

    }

    public Float getRacioOfDistrict(String distrito){
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getRatio";


        socket.send(str.getBytes(ZMQ.CHARSET),0);
        resposta =new String(socket.recv(), StandardCharsets.UTF_8);

        System.out.println(resposta);
        float idk = 2;
        try {
             idk = Float.parseFloat(resposta);
        }catch (Exception e){e.printStackTrace();
        }

        return idk ;

    }

    public List<Location> getLocationOfDistrictWithMostPeople(String distrito){
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getLocationTop";

        socket.send(str);

        resposta = socket.recvStr();
        // string 1-1-5,1-2-10

        List<Location> answer = new ArrayList<>();

        String [] parser = resposta.split(",");
        for(String s :parser){
            String[] elem = s.split("-");
            if(elem[0].isEmpty() ||elem[1].isEmpty() || elem[2].isEmpty())continue;
            Location loc = new Location(Integer.parseInt(elem[0]),Integer.parseInt(elem[1]),Integer.parseInt(elem[2]),distrito);
            answer.add(loc);
        }
        return answer;

    }

    public float getUsersWhoCrossedWithSickPeople(String distrito){
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getUsersCrossed";

        socket.send(str);

        resposta = socket.recvStr();

        return Float.parseFloat(resposta);

    }

}
