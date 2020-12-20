import java.util.*;

public class ServidorDistrital {
    private final String nome;
    private final int aresta;
    private List<Integer>[][] mapa; //matriz de ArrayLists de IDs dos users, para cada localização do distrito
    private Map<Integer,ArrayList<Integer>> contactos;
    private List<Integer> notificacoes; //pseudo lista de users->sockets a notificar
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

    public List<Integer> getNotificacoes() {
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
        return mapa[x][y].size();
    }

    public void subscreverNotificacoes(int user) {
        if(!this.notificacoes.contains(user))
            this.notificacoes.add(user);
    }

    public void retirarNotificacoes(int user) {
        if(this.notificacoes.contains(user))
            this.notificacoes.remove(user);
    }

    public void moveTo (int user, int x, int y){
        // retirar da antiga localização
        int oldX, oldY;
        for (int row = 0; row < aresta; row++) {
            for (int col = 0; col < aresta; col++) {
                if(mapa[row][col].contains(user)){
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
        if(!mapa[x][y].contains(user)){
            this.atualizarContactos(user,x,y);
            mapa[x][y].add(user);
            // notificar entrada em localização
            notifEntradaLocal(x,y);
        }
    }

    private void atualizarContactos(int user, int x, int y) {
        List<Integer> temp = mapa[x][y];
        ArrayList<Integer> contactosUser = contactos.get(user);
        for(int elem : temp){
            if(!contactosUser.contains(elem)) contactos.get(user).add(elem);
            if(!contactos.get(elem).contains(user)) contactos.get(elem).add(user);
        }
    }

    private void notifSaidaLocal(int x, int y) {
        for(int elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifEntradaLocal(int x, int y) {
        for(int elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifLocalVazia(int x, int y) {
        for(int elem : notificacoes){
            //notificar subscritos de um distrito
        }
    }

    private void notifInfetado(int infetado) {
        this.numInfetados++;
        for(int elem : notificacoes){
            //notificar subscritos de um distrito
        }
        for(int contacto : contactos.get(infetado)){
            // notificar todos os contactos
        }
    }

}
