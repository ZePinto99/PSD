package Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

        context = new ZContext();
        socketList = new ArrayList<>();

        for(int i = 0; i < Distrito.values().length;i++){

            ZMQ.Socket socket = context.createSocket(SocketType.REQ);

            int port = portDefault + i;

            socket.bind("tcp://*:" + port);

            socketList.add(socket);
        }


    }

    public static DistrictService getInstance()
    {
        if (inst == null)
            inst = new DistrictService();

        return inst;
    }


    public int getNumberOfUsersInDistrict(String distrito) {
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getNumUsers";

        socket.send(str);

        resposta = socket.recvStr();


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

        socket.send(str);

        resposta = socket.recvStr();

        return Float.parseFloat(resposta);

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
            Location loc = new Location(Integer.parseInt(elem[0]),Integer.parseInt(elem[1]),Integer.parseInt(elem[2]),distrito);
            answer.add(loc);
        }
        return answer;

    }

    public int getUsersWhoCrossedWithSickPeople(String distrito){
        String resposta;

        ZMQ.Socket socket = socketList.get(Distrito.findDistrictPosition(distrito));

        String str = "getUsersCrossed";

        socket.send(str);

        resposta = socket.recvStr();

        return Integer.parseInt(resposta);

    }

}
