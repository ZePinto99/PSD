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

    public ServidorDistrital(String nome, int aresta) {
        this.nome = nome;
        this.aresta = aresta;
        this.mapa = new ArrayList[aresta][aresta];
        this.contactos = new HashMap<>();
        this.notificacoes = new ArrayList<>();
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

    public void moveTo (String user, int x, int y){
        // descobrir e retirar da antiga localização
        int oldX = -1, oldY = -1;
        boolean primeiraEntrada = true;
        for (int row = 0; row < aresta; row++) {
            for (int col = 0; col < aresta; col++) {
                if(mapa[row][col] != null && mapa[row][col].contains(user)){
                    primeiraEntrada = false;
                    oldX = row; oldY = col;
                    mapa[row][col].remove(user);
                    // notificar caso a saída da localização
                    notifSaidaLocal(oldX,oldY);
                    // notificar caso a localização fique vazia
                    if(mapa[oldX][oldY].isEmpty()) notifLocalVazia(oldX,oldY);
                    break;
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
        }
        // notificar entrada em localização
        notifEntradaLocal(x,y);
    }

    private void atualizarContactos(String user, int x, int y) {
        List<String> temp = mapa[x][y];
        ArrayList<String> contactosUser = contactos.get(user);
        for(String elem : temp){
            if(!contactosUser.contains(elem)) contactos.get(user).add(elem);
            if(!contactos.get(elem).contains(user)) contactos.get(elem).add(user);
        }
    }

    public void notifSaidaLocal(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    public void notifEntradaLocal(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    public void notifLocalVazia(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    public String notifInfetado(String infetado) {
        this.numInfetados++;
        ArrayList<String> list = contactos.get(infetado);
        String rep = list.stream().map(String::valueOf).collect(Collectors.joining(","));
        return rep;
    }

}
