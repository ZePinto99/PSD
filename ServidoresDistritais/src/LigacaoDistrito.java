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

            ServidorDistrital sd = new ServidorDistrital(args[1],10);

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
}


