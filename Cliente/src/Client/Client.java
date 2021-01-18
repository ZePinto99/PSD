package Client;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Thread.sleep;

public class Client {
    private static String myname;
    private static String mypass;

    public static boolean validXY(String x, String y){
        try {
            if (Integer.parseInt(x) <= 20 && Integer.parseInt(x) >= 0 && Integer.parseInt(y) <= 20 && Integer.parseInt(y) >= 0)
                return true;
        }catch (Exception e){return false;}
        return false;
    }

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
                        String distrito = "";
                        while(true) {
                            System.out.println("Inserir distrito:");
                            distrito = input.readLine();
                            List<String> distritos = Arrays.asList("Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre");
                            if (distritos.contains(distrito)) break;
                            else System.out.println("Distrito invalido.\n lista de distritos válidos: " + distritos);
                        }
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
        if(!reply.equals("invalid_password") && !reply.equals("invalid_username") && !reply.equals("User Bloqueado. Mantenha as normas da DGS e continue em isolamento.")){
            List<String> districts = new ArrayList<>();
            System.out.println("Login feito com sucesso");
            String hear = "?" + username + "?";
            beginNotifications(subscriber, hear);
            districts.add(hear);
            String[] arrOfStr = reply.split(",");
            int i =0;
            while(!arrOfStr[i].equals("end")){
                districts.add(arrOfStr[i]);
                beginNotifications(subscriber, arrOfStr[i]);
                i++;
            }

            districts= menu (input, requester, subscriber,districts);
            for (String unsub : districts) {
                subscriber.unsubscribe(unsub);
            }
        }
        else {
            if(reply.equals("User Bloqueado. Mantenha as normas da DGS e continue em isolamento."))
                System.out.println("User Bloqueado. Mantenha as normas da DGS e continue em isolamento.");
            else System.out.println("Credênciais erradas");
        }
    }

    private static List<String> menu(BufferedReader input, ZMQ.Socket requester, ZMQ.Socket subscriber, List<String> districts) throws IOException {

        System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações \n5- Número de Users num distrito 6- Número de infetados num distrito 7- Ratio de infetados num distrito \n8- Top 5 de posições que tiveram mais users 9- Número médio de Crossings com infetados pelos distritos");
        String  option = input.readLine();

        boolean aux = true;
        String x, y;
        String args;
        String reply;
        while (aux) {
            switch (option) {
                case "0":
                    aux = false;
                    args = "quit,"+myname+ "," +mypass;
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    return districts;
                case "1":
                    x=y="-1";
                    while(!validXY(x,y)) {
                        System.out.println("Escreva as suas coordenadas. apenas serão consideradas validas se o valor se encontrar entre 0 e 20.");
                        System.out.println("Inserir coordenada x:");
                        x = input.readLine();
                        System.out.println("Inserir coordenada y:");
                        y = input.readLine();
                    }
                    args = "localizacao,"+myname+ "," +mypass + "," + x + "," + y;
                    requester.send(args.getBytes(ZMQ.CHARSET),0);
                    //receber possível notificação de alteração
                    reply =new String(requester.recv(), StandardCharsets.UTF_8);
                    if(reply.equals("ok")){
                        System.out.println("A sua posição foi atualizada");
                    }
                    else
                        System.out.println("ERROR: Posição não atualizada");
                    break;
                case "2":
                    x=y="-1";
                    while(!validXY(x,y)) {
                        System.out.println("Escreva as suas coordenadas. apenas serão consideradas validas se o valor se encontrar entre 0 e 20.");
                        System.out.println("Inserir coordenada x:");
                        x = input.readLine();
                        System.out.println("Inserir coordenada y:");
                        y = input.readLine();
                    }
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
                        aux = false;
                        return districts;
                    }
                    break;
                case "4":
                    System.out.println("Quer ativar/desativar as notificações?\n Pressione '1' para ativar, Pressione '2' se sim desativar");
                    String notifications = input.readLine();
                    if (notifications.equals("1")){
                        System.out.println("Qual distrito quer subscreber?");
                        String distrito = "";
                        while(true) {
                            distrito = input.readLine();
                            List<String> distritos = Arrays.asList("Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre");
                            if (distritos.contains(distrito)) break;
                            else System.out.println("Distrito invalido.\n lista de distritos válidos: " + distritos);
                        }
                        if(!districts.contains(distrito)) {
                            args = "ativar" + "," + myname + "," + mypass + "," + distrito;
                            requester.send(args.getBytes(ZMQ.CHARSET), 0);
                            reply = new String(requester.recv(), StandardCharsets.UTF_8);
                            if (reply.equals("ok")) { //O servidor manda ok se o cliente estiver subscrito a menos de 3 distritos
                                districts.add(distrito);
                                beginNotifications(subscriber, distrito);
                                System.out.println("Notificações públicas para o distrito " + distrito + " ativadas");
                            } else
                                System.out.println("ERROR: Não foi possível ativar notificações públicas");
                            break;
                        }else {
                            System.out.println("Ja subscreveu esse distrito"); break;
                        }
                    }
                    else {
                        if(notifications.equals("2") ) {
                            System.out.println("Qual distrito quer deixar de subscrever?");
                            String distrito = input.readLine();
                            if(districts.contains(distrito)) {
                                args = "desativar" + "," + myname + "," + mypass + "," + distrito;
                                subscriber.unsubscribe(distrito.getBytes());
                                requester.send(args.getBytes(ZMQ.CHARSET), 0);
                                reply = new String(requester.recv(), StandardCharsets.UTF_8);
                                break;
                            }
                            else {System.out.println("Não tem esse distrito subscrito"); break;}
                        }
                        else break;
                    }
                case "5":
                    System.out.println("Qual distrito quer subscreber?");
                    String distrito = "";
                    while(true) {
                        distrito = input.readLine();
                        List<String> distritos = Arrays.asList("Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre");
                        if (distritos.contains(distrito)) break;
                        else System.out.println("Distrito invalido.\n lista de distritos válidos: " + distritos);
                    }

                    URL url = new URL("http://localhost:8080/User?distrito=" + distrito);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    con.connect();
                    System.out.println(con.getResponseMessage());
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        System.out.println(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    break;
                case "6":
                    System.out.println("Qual distrito quer subscreber?");
                    distrito = "";
                    while(true) {
                        distrito = input.readLine();
                        List<String> distritos = Arrays.asList("Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre");
                        if (distritos.contains(distrito)) break;
                        else System.out.println("Distrito invalido.\n lista de distritos válidos: " + distritos);
                    }
                    url = new URL("http://localhost:8080/Infected?distrito=" + distrito);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    con.connect();
                    System.out.println(con.getResponseMessage());
                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        System.out.println(inputLine);
                    }
                    
                    in.close();
                    con.disconnect();
                    break;
                case "7":
                    url = new URL("http://localhost:8080/Racio");
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    con.connect();
                    System.out.println(con.getResponseMessage());
                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        System.out.println(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    break;
                case "8":
                    url = new URL("http://localhost:8080/Locations");
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    con.connect();
                    System.out.println(con.getResponseMessage());
                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        System.out.println(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    break;
                case "9":
                    url = new URL("http://localhost:8080/Disease");
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    con.connect();
                    System.out.println(con.getResponseMessage());
                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                        System.out.println(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    break;

            }
            System.out.println("0-quit 1-Nova localização 2-Nr pessoas por localização 3-Estou infetado! 4-Subscrição de Notificações \n5- Número de Users num distrito 6- Número de infetados num distrito 7- Ratio de infetados num distrito \n8- Top 5 de posições que tiveram mais users 9- Número médio de Crossings com infetados pelos distritos");
            option = input.readLine();
        }
        return districts;
    }

    private static void beginNotifications(ZMQ.Socket socket, String hear){
        String[] args = {"9999"};
        socket.connect("tcp://localhost:" + args[0]);
        Thread t = new Thread(() -> {
            try {
                if (args.length == 1)
                    socket.subscribe(hear.getBytes());
                else for (int i = 1; i < args.length; i++)
                    socket.subscribe(hear.getBytes());
                while (true) {
                    byte[] msg = socket.recv();
                    sleep(200);
                    System.out.println(new String(msg));
                }
            }
            catch (Exception e){;return;}
        });
        t.start();
        return;
    }
}


