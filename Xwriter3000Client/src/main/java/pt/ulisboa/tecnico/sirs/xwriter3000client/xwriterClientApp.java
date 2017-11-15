package pt.ulisboa.tecnico.sirs.xwriter3000client;


//this main was just for testing the connection
public class xwriterClientApp {

    public static void main(String[] args) {
        CommunicationClient communicationClient = new CommunicationClient();

        communicationClient.createBook("test", "goodBook");

    }

}
