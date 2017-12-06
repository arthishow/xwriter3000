package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class xwriterServerApp {

    public static void main(String[] args) throws Exception {

        if(args[0].equals("recovery")) {
            RecoveryServer xwriterRecoveryServer = new RecoveryServer("localhost", 8006);
            xwriterRecoveryServer.run();
        }else if(args[0].equals("main")) {
            Server xwriterServer = new Server(8005,"localhost", 8006);
            xwriterServer.run();
        }
    }
}
