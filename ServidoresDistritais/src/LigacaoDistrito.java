import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;

public class LigacaoDistrito {

    public static void main(String[] args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            ZMQ.Socket replyer = context.createSocket(SocketType.REP);
            replyer.bind("tcp://127.0.0.1:" + args[0]);
            System.out.println("tcp://127.0.0.1:" + args[0]);

            int port = Integer.parseInt(args[0]);
            ServidorDistrital sd = new ServidorDistrital(args[1],21);

            Diretorio myThread = new Diretorio(sd,port);
            myThread.start();
            boolean aux = true;
            while (aux) {
                String option = new String(replyer.recv(), StandardCharsets.UTF_8);
                System.out.println(option);

                String[] arrOfStr = option.split(",");
                String request = arrOfStr[0];
                switch (request) {
                    case "localizacao":
                        String reply = sd.moveTo(arrOfStr[1],Integer.parseInt(arrOfStr[2]),Integer.parseInt(arrOfStr[3]));
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "infoLocalizacao":
                        replyer.send(String.valueOf(sd.getNumUsersLocalizacao(Integer.parseInt(arrOfStr[2]),Integer.parseInt(arrOfStr[3]))).getBytes(ZMQ.CHARSET),0);
                        break;
                    case "infetado":
                        String rep = sd.notifInfetado(arrOfStr[1]);
                        replyer.send(rep.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "ativar":
                        break;
                    case "desativar":
                        break;
                }
            }
        }
    }

    public static class Diretorio extends Thread {

        ServidorDistrital sd;
        String port;

        public Diretorio(ServidorDistrital s, int porta ){
            sd = s;
            port = String.valueOf(porta + 30);
            System.out.println(port);
        }


        public void run(){
            ZContext context = new ZContext();
            ZMQ.Socket replyer = context.createSocket(SocketType.REP);
            replyer.bind("tcp://127.0.0.1:" + port);
            System.out.println("tcp://127.0.0.1:" + port);
            boolean aux = true;
            while (aux) {
                String option = new String(replyer.recv(), StandardCharsets.UTF_8);
                System.out.println(option);

                String[] arrOfStr = option.split(",");
                String request = arrOfStr[0];
                switch (request) {
                    case "getNumUsers":
                        String reply = String.valueOf(sd.getNumUtilizadores());
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "getNumInfected":
                        reply = String.valueOf(sd.getNumInfetados());
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "getRatio":
                        int infetados    = sd.getNumInfetados();
                        int utilizadores = sd.getNumUtilizadores();
                        float f = (float) infetados/utilizadores;
                        if(infetados == 0 && utilizadores == 0)f = 0;
                        reply = String.valueOf(f);
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "getLocationTop":
                        reply = sd.top5posicao();
                        System.out.println(reply);
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "getUsersCrossed":
                        reply = String.valueOf(sd.getCrossings());
                        replyer.send(reply.getBytes(ZMQ.CHARSET),0);
                        break;
                    case "desativar":
                        break;
                }
            }
        }
    }
}


