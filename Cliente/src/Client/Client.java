package Client;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            ZMQ.Socket requester = context.createSocket(SocketType.REQ);
            requester.connect("tcp://127.0.0.1:12345");
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

    //Nota um request tem de ser seguido de um reply
    private static void registar(String username, String password, String distrito, ZMQ.Socket requester){
        String args = "registar," + username + "," + password+ "," + distrito;
        requester.send(args.getBytes(ZMQ.CHARSET),0);
        //receber resposta
        String reply =new String(requester.recv(), StandardCharsets.UTF_8);
        System.out.println(reply);
        if(reply.equals("ok")){
            System.out.println("Foi registado com sucesso");
        }
        else
            System.out.println("ERROR: Credênciais ocupadas");
    }
    //Nota um request tem de ser seguido de um reply
    private static void login(String username, String password, BufferedReader input, ZMQ.Socket requester) throws IOException {
        String args = "login," + username + "," + password;
        requester.send(args.getBytes(ZMQ.CHARSET),0);
        //receber resposta
        String reply =new String(requester.recv(), StandardCharsets.UTF_8);
        if(reply.equals("ok")){
            System.out.println("Login feito com sucesso");
            menu(input, requester);
        }
        else
            System.out.println("Credênciais erradas");
    }

    private static void menu(BufferedReader input, ZMQ.Socket requester) throws IOException {
        System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
        String  option = input.readLine();

        boolean aux = true;
        int x, y;
        String args;
        String reply;
        while (aux) {
            System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
            option = input.readLine();
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
                    args = "localizacao," + x + "," + y + "}";
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    System.out.println(reply);
                    if(reply.equals("ok")){
                        System.out.println("A sua posição foi atualizada");
                    }
                    else
                        System.out.println("ERROR: Posição não atualizada");
                    break;
                case "2":
                    System.out.println("Inserir coordenada x:");
                    x = Integer.parseInt(input.readLine());
                    System.out.println("Inserir coordenada y:");
                    y = Integer.parseInt(input.readLine());
                    args = "infoLocalizacao," + x + "," + y + "}";
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    System.out.println(reply);
                    if(reply.equals("ok")){
                        System.out.println("Not pessoas na loc");
                    }
                    else
                        System.out.println("ERROR: Busy server cant respond");
                    break;
                case "3":
                    System.out.println("Quer confirmar que está infetado?\n Pressione 'Y' se sim");
                    String confirmation = input.readLine();
                    if (confirmation.equals("y") || confirmation.equals("Y")){
                        args = "infetado";
                        requester.send(args.getBytes(ZMQ.CHARSET),0);
                        //receber possível notificação de alteração
                        reply =new String(requester.recv(), StandardCharsets.UTF_8);
                        System.out.println(reply);
                        if(reply.equals("ok")){
                            System.out.println("Foi registado com sucesso");
                        }
                        else
                            System.out.println("ERROR: Credênciais ocupadas");
                        aux = false;
                        break;
                    }
                case "4":
                    System.out.println("Quer ativar as notificações?\n Pressione 'Y' se sim");
                    String notifications = input.readLine();
                    if (notifications.equals("y") || notifications.equals("Y")){
                        requester.send("ativar".getBytes(ZMQ.CHARSET),0);
                        reply =new String(requester.recv(), StandardCharsets.UTF_8);
                        System.out.println(reply);
                        if(reply.equals("ok")){
                            System.out.println("Notificações públicas ativadas");
                        }
                        else
                            System.out.println("ERROR: Não foi possível ativar notificações públicas");
                        break;
                    }
                    else {
                        requester.send("desativar".getBytes(ZMQ.CHARSET),0);
                        reply =new String(requester.recv(), StandardCharsets.UTF_8);
                        System.out.println(reply);
                        if(reply.equals("ok")){
                            System.out.println("Notificações públicas desativadas");
                        }
                        else
                            System.out.println("ERROR: Não foi possível desativar notificações públicas");
                        break;
                    }
            }
        }
    }
}


