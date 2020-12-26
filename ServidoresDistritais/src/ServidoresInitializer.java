public class ServidoresInitializer {

    public static void main(String args[]) {
        int port = 12346;
        for(int i = 0; i<20;i++)
        {
            LigacaoDistrito distrito = new LigacaoDistrito();
            String[] arguments = new String[] {String.valueOf(port+i)};
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    distrito.main(arguments);
                }
            });
            thread.start();
        }
    }

}
