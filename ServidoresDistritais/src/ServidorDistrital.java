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
    private Integer[][] maxPorLocalizacao; //matriz de ArrayLists de IDs dos users, para cada localização do distrito


    public ServidorDistrital(String nome, int aresta) {
        this.nome = nome;
        this.aresta = aresta;
        this.mapa = new ArrayList[aresta][aresta];
        this.contactos = new HashMap<>();
        this.notificacoes = new ArrayList<>();
        this.maxPorLocalizacao = new Integer[aresta][aresta];
        for(int i=0; i<aresta; i++)
            for(int j=0; j<aresta; j++)
                maxPorLocalizacao[i][j]=0;
    }

    public String top5posicao() {

        HashMap<String,Integer> mapAux = new HashMap<>();

        for(int x=0; x<aresta; x++) {
            for (int y = 0; y < aresta; y++) {
                String posicao = x + "-" + y;
                mapAux.put(posicao, maxPorLocalizacao[x][y]);
            }
        }

        List<String> top5localizacoes = topNKeys(mapAux,5);

        String result = "";
        for(int i = 0; i<5; i++)   {
            String posicao = top5localizacoes.get(i);
            result+= posicao + "-" + mapAux.get(posicao) + ",";
        }
        return result;
    }

    private List<String> topNKeys(final HashMap<String, Integer> map, int n) {

        PriorityQueue<String> topN = new PriorityQueue<String>(n, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Integer.compare(map.get(s1), map.get(s2));
            }
        });

        for (String key : map.keySet()) {
            if (topN.size() < n)
                topN.add(key);
            else if (map.get(topN.peek()) < map.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        return (List) Arrays.asList(topN.toArray());
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
            this.atualizarMax(x,y);
            if(primeiraEntrada)
                rep ="entrou,acabou";
        }
        // notificar entrada em localização

        return rep;
    }

    private void atualizarMax(int x, int y){
        int sizeAtual = mapa[x][y].size();
        int sizeMax = maxPorLocalizacao[x][y];
        if(sizeAtual > sizeMax) this.maxPorLocalizacao[x][y] = sizeAtual;
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
