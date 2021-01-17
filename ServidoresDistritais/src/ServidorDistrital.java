import org.zeromq.ZMQ;

import java.util.*;
import java.util.stream.Collectors;

public class ServidorDistrital {
    private final String nome;
    private final int aresta;
    private List<String>[][] mapa; //matriz de ArrayLists de IDs dos users, para cada localização do distrito
    private Map<String,ArrayList<String>> contactos; //utilizador -> lista contactos que teve
    private List<String> notificacoes; //pseudo lista de users->sockets a notificar
    private int numUtilizadores;
    private int numInfetados;
    private int crossings;

    public ServidorDistrital(String nome, int aresta) {
        this.nome = nome;
        this.aresta = aresta;
        this.mapa = new ArrayList[aresta][aresta];
        this.contactos = new HashMap<>();
        this.notificacoes = new ArrayList<>();
    }

    public String top5posicao() {
        List<String> posicao = new ArrayList<>();
        List<Integer> numerodepeeps = new ArrayList<>();

        for (int row = 0; row < aresta; row++)
            for (int col = 0; col < aresta; col++) {
                for (int top5 = 0; top5 < 5 && top5 < posicao.size(); top5++) {
                    if (mapa[row][col] != null) {
                        if (mapa[row][col].size() > numerodepeeps.get(top5)) {
                            posicao.add(top5, row + "-" + col);
                            numerodepeeps.add(top5, mapa[row][col].size());
                            break;
                        }
                    }
                }
            }

        String result = "";
        for(int i = 0; i<5;i++)   {
            result+= posicao.get(i)+"-"+numerodepeeps.get(i)+",";
        }
        return result;

    }
    public int getCrossings(){
        return crossings;
    }

    public String getNome() {
        return nome;
    }

    public int getAresta() {
        return aresta;
    }

    public String getNotificacoes() {
        String rep = this.notificacoes.stream().map(String::valueOf).collect(Collectors.joining(","));
        return rep;
    }

    public int getNumUtilizadores() {
        return numUtilizadores;
    }

    public void entradaUtilizador() {
        this.numUtilizadores++;
    }

    public int getNumInfetados() {
        return numInfetados;
    }

    public int getNumUsersLocalizacao(int x, int y) {
        try {return mapa[x][y].size();}
        catch (Exception e){ return 0;}
    }

    public void subscreverNotificacoes(String user) {
        if(!this.notificacoes.contains(user))
            this.notificacoes.add(user);
    }

    public void retirarNotificacoes(String user) {
        if(this.notificacoes.contains(user))
            this.notificacoes.remove(user);
    }

    public String moveTo (String user, int x, int y){
        // descobrir e retirar da antiga localização
        int oldX = -1, oldY = -1;
        boolean primeiraEntrada = true;
        String rep = "ficou,acabou";
        for (int row = 0; row < aresta; row++) {
            for (int col = 0; col < aresta; col++) {
                if(mapa[row][col] != null && mapa[row][col].contains(user)){
                    if( row != x && col != y){
                        primeiraEntrada = false;
                        oldX = row; oldY = col;
                        mapa[row][col].remove(user);
                        // notificar caso a localização fique vazia
                        if(mapa[oldX][oldY].isEmpty()) rep = "vazia," + String.valueOf(oldX) + "," + String.valueOf(oldY) + ",";
                        else rep ="saiu," + String.valueOf(oldX) + "," + String.valueOf(oldY) + ",";
                        break;}
                    else break;
                }
            }
        }
        // primeira entrada no distrito
        if(primeiraEntrada) entradaUtilizador();
        // mover para nova localização
        if(mapa[x][y] == null) mapa[x][y] = new ArrayList<>();
        if(!mapa[x][y].contains(user)){
            this.atualizarContactos(user, x, y);
            mapa[x][y].add(user);
            if(primeiraEntrada)
                rep ="entrou,acabou";
        }
        // notificar entrada em localização

        return rep;
    }

    private void atualizarContactos(String user, int x, int y) {
        List<String> temp = mapa[x][y];
        ArrayList<String> contactosUser = contactos.get(user);
        for(String elem : temp){
            if(contactosUser!=null && !contactosUser.contains(elem)){
                ArrayList<String> userlist = contactos.get(user);
                userlist.add(elem);
                contactos.put(user,userlist);
            }else{
                contactos.put(user,new ArrayList<>());
                ArrayList<String> userlist = contactos.get(user);
                userlist.add(elem);
                contactos.put(user,userlist);
            }
            if(contactos.get(elem)!= null && !contactos.get(elem).contains(user)){
                ArrayList<String> elemlist = contactos.get(elem);
                elemlist.add(user);
                contactos.put(elem,elemlist);
            }else{
                contactos.put(elem,new ArrayList<>());
                ArrayList<String> elemlist = contactos.get(elem);
                elemlist.add(user);
                contactos.put(elem,elemlist);
            }
        }
    }

    public String notifInfetado(String infetado) {
        this.numInfetados++;
        String rep="";
        if(contactos !=null && contactos.get(infetado)!=null) {
            crossings += contactos.get(infetado).size();
            ArrayList<String> list = contactos.get(infetado);
            rep = list.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        return rep;
    }

}
