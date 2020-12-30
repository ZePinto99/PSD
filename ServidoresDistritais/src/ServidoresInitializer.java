public class ServidoresInitializer {

    public static void main(String args[]) {
        int port = 12346;
        for(int i = 0; i<20;i++)
        {
            String[] distritos = {"Lisboa", "Porto", "Braga", "Setubal", "Aveiro", "Faro", "Leiria", "Coimbra", "Santarem", "Viseu", "Madeira", "Acores", "Viana Do Castelo", "Vila Real", "Castelo Branco", "Evora", "Guarda", "Beja", "Braganca", "Portalegre"};
            LigacaoDistrito distrito = new LigacaoDistrito();
            String[] arguments = new String[] {String.valueOf(port+i),distritos[i]};
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    distrito.main(arguments);
                }
            });
        }
    }

}
