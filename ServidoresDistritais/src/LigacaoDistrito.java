import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class LigacaoDistrito {
    private ServidorDistrital sd;

    public static void main(String args[]) {
        System.out.println(args[0]);
        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            String distritos[] = {"Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santaréqm", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Évora", "Guarda", "Beja", "Bragança", "Portalegre"};
            System.out.println(distritos.length);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Qual distrito pretende?\n" + Arrays.toString(distritos));

            ZMQ.Socket replyer = context.createSocket(SocketType.REP);
            replyer.connect("tcp://127.0.0.1:" + args[0] + + Integer.parseInt(input.readLine()));
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            //subscriber.connect(F.Server.BOUNDED_ADDRESS);

            boolean aux = true;
            while (aux) {
                System.out.println("Hello there");
                String option = new String(replyer.recv(), StandardCharsets.UTF_8);
                switch (option) {
                    case "localizacao":
                        break;
                    case "nrPessoas":
                        break;
                    case "infetado":
                        aux = false;
                        break;
                    case "ativar":
                        break;
                    case "desativar":
                        break;
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
