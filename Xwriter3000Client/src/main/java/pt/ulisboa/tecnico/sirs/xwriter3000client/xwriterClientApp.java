package pt.ulisboa.tecnico.sirs.xwriter3000client;


//this main was just for testing the connection
public class xwriterClientApp {

    public static void main(String[] args) {
        CommunicationClient communicationClient = new CommunicationClient();

        System.out.println(communicationClient.authenticateUser("test", "goodPass"));



    }

}
