package Client;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private static String myname;
    private static String hear = "?" + myname + "?";
    private static String mypass;

    public static void main(String[] dab) throws IOException {

        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            ZMQ.Socket requester = context.createSocket(SocketType.REQ);
            String processName =
                    java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
           long pid =  Long.parseLong(processName.split("@")[0]);
            requester.setIdentity(String.valueOf(pid).getBytes());
            requester.connect("tcp://127.0.0.1:12345");


            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);


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
                        myname=username;
                        System.out.println("Inserir password:");
                        password = input.readLine();
                        mypass=password;
                        login(username, password, input, requester, subscriber);
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
    private static void login(String username, String password, BufferedReader input, ZMQ.Socket requester, ZMQ.Socket subscriber) throws IOException {
        String args = "login," + username + "," + password;
        requester.send(args.getBytes(ZMQ.CHARSET),0);
        //receber resposta
        String reply =new String(requester.recv(), StandardCharsets.UTF_8);
        if(reply.equals("ok")){
            System.out.println("Login feito com sucesso");
            beginNotifications(subscriber);
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
            switch (option) {
                case "0":
                    aux = false;
                    args = "quit,"+myname+ "," +mypass;
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    System.out.println(reply);
                    return;
                case "1":
                    System.out.println("Inserir coordenada x:");
                    x = Integer.parseInt(input.readLine());
                    System.out.println("Inserir coordenada y:");
                    y = Integer.parseInt(input.readLine());
                    //enviar novas cooredenadas
                    args = "localizacao,"+myname+ "," +mypass + "," + x + "," + y;
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
                    args = "infoLocalizacao," +myname+ "," +mypass + "," + x + "," + y;
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    System.out.println(reply);
                    break;
                case "3":
                    System.out.println("Quer confirmar que está infetado?\n Pressione 'Y' se sim");
                    String confirmation = input.readLine();
                    if (confirmation.equals("y") || confirmation.equals("Y")){
                        args = "infetado" + "," + myname + "," + mypass;
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
            System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações");
            option = input.readLine();
        }
    }

    private static void beginNotifications(ZMQ.Socket socket){
        String[] args = {"9999"};
        socket.connect("tcp://localhost:" + args[0]);

        Thread t = new Thread(() -> {
            if (args.length == 1)
                socket.subscribe(hear.getBytes());
            else for (int i = 1; i < args.length; i++)
                socket.subscribe(args[i].getBytes());
            while (true) {
                byte[] msg = socket.recv();
                System.out.println(new String(msg));
            }
        });
        t.start();
    }
}


