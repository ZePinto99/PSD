package Client;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    public static void main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            ZMQ.Socket requester = context.createSocket(SocketType.REQ);
            //requester.connect(F.Server.BOUNDED_ADDRESS);
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            //subscriber.connect(F.Server.BOUNDED_ADDRESS);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            boolean aux = true;
            while (aux) {
                System.out.println("Hello there");
                System.out.println("1-login 2-registar 3-quit");
                String option = input.readLine();
                String username;
                String password;
                switch (option) {
                    case "1":
                        System.out.println("Inserir :");
                        username = input.readLine();
                        System.out.println("Inserir password:");
                        password = input.readLine();
                        login(username, password, input, requester);
                        break;
                    case "2":
                        System.out.println("Inserir :");
                        username = input.readLine();
                        System.out.println("Inserir password:");
                        password = input.readLine();
                        System.out.println("Inserir distrito:");
                        String distrito = input.readLine();
                        registar(username, password, distrito, requester);
                        break;
                    case "3":
                        aux = false;
                        break;
                }
            }

            input.close();
        }

    }

    private static void registar(String username, String password, String distrito, ZMQ.Socket requester){
        String args = "{registar, " + username + ", " + password+ ", " + distrito + "}";
        requester.send(args.getBytes(ZMQ.CHARSET),0);
        //receber resposta
        //if registado
        //else
    }

    private static void login(String username, String password, BufferedReader input, ZMQ.Socket requester) throws IOException {
        String args = "{login, " + username + ", " + password + "}";
        requester.send(args.getBytes(ZMQ.CHARSET),0);
        //receber resposta
        //if autenticado
        menu(input, requester);
        //else
    }

    private static void menu(BufferedReader input, ZMQ.Socket requester) throws IOException {
        String  option = input.readLine();
        System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");

        boolean aux = true;
        int x, y;
        String args;
        while (aux) {
            switch (option) {
                case "0":
                    aux = false;
                    break;
                case "1":
                    System.out.println("Inserir coordenada x:");
                    x = Integer.parseInt(input.readLine());
                    System.out.println("Inserir coordenada y:");
                    y = Integer.parseInt(input.readLine());
                    //enviar novas cooredenadas
                    args = "{localizacao, " + x + ", " + y + "}";
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    //if autenticado
                    //else
                    break;
                case "2":
                    System.out.println("Inserir coordenada x:");
                    x = Integer.parseInt(input.readLine());
                    System.out.println("Inserir coordenada y:");
                    y = Integer.parseInt(input.readLine());
                    args = "{localizacao, " + x + ", " + y + "}";
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    //if autenticado
                    //else
                    break;
                case "3":
                    System.out.println("Quer confirmar que está infetado?\n Pressione 'Y' se sim");
                    String confirmation = input.readLine();
                    if (confirmation.equals("y") || confirmation.equals("Y")){
                        args = "{infetado" + "}";
                        requester.send(args.getBytes(ZMQ.CHARSET),0);
                        //receber possível notificação de alteração
                        //if autenticado
                        //else
                        aux = false;
                        break;
                    }
                case "4":
                    System.out.println("Quer ativar as notificações?\n Pressione 'Y' se sim");
                    String notifications = input.readLine();
                    if (notifications.equals("y") || notifications.equals("Y")){
                        requester.send("{ativar}".getBytes(ZMQ.CHARSET),0);
                        break;
                    }
                    else {
                        requester.send("{desativar}".getBytes(ZMQ.CHARSET),0);
                    }
            }
        }
    }
}


