package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.net.InetAddress;

public class xwriterServerApp {

    public static void main(String[] args) throws Exception {

        Server xwriterServer = new Server(8001, "localhost", 8002, false);

        xwriterServer.run();
    }
}
