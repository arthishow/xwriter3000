package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class xwriterServerApp {

    public static void main(String[] args) throws Exception {

        Server xwriterServer = new Server(8005);

        xwriterServer.run();
    }
}
