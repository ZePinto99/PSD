import java.util.*;

public class ServidorDistrital {
    private final String nome;
    private final int aresta;
    private List<String>[][] mapa; //matriz de ArrayLists de IDs dos users, para cada localização do distrito
    private Map<String,ArrayList<String>> contactos;
    private List<String> notificacoes; //pseudo lista de users->sockets a notificar
    private int numUtilizadores;
    private int numInfetados;

    public ServidorDistrital(String nome, int aresta) {
        this.nome = nome;
        this.aresta = aresta;
        this.mapa = new ArrayList[100][100];
        this.contactos = new HashMap<>();
        this.notificacoes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getAresta() {
        return aresta;
    }

    public List<String> getNotificacoes() {
        return notificacoes;
    }

    public int getNumUtilizadores() {
        return numUtilizadores;
    }

    public void entradaUtilizador(int utilizadores) {
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
        // retirar da antiga localização
        int oldX, oldY;
        for (int row = 0; row < aresta; row++) {
            for (int col = 0; col < aresta; col++) {
                if(mapa[row][col] != null && mapa[row][col].contains(user)){
                    oldX = row; oldY = col;
                    mapa[row][col].remove(user);
                    // notificar caso a localização fique vazia
                    notifSaidaLocal(oldX,oldY);
                    if(mapa[oldX][oldY].isEmpty()) notifLocalVazia(oldX,oldY);
                    break;
                }
            }
        }
        // mover para nova localização
        if(mapa[x][y] != null && !mapa[x][y].contains(user)){
            this.atualizarContactos(user,x,y);

        }
        mapa[x][y] = new ArrayList<>();
        mapa[x][y].add(user);
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

    private void notifSaidaLocal(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifEntradaLocal(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifLocalVazia(int x, int y) {
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifInfetado(int infetado) {
        this.numInfetados++;
        for(String elem : notificacoes){
            //notificar subscritos de um distrito
        }
        for(String contacto : contactos.get(infetado)){
            // notificar todos os contactos
        }
    }

}
