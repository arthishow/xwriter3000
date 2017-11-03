package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class xwriterServerApp {

    public static void main(String[] args) throws Exception{

        CommunicationServer server = new CommunicationServer(8000);

        try {
            server.run();
        } catch (Exception e){
            System.out.println("Problem");
        }
    }
}
