package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000ui.Book;

import java.util.List;

//this main was just for testing the connection
public class xwriterClientApp {

    public static void main(String[] args) {
        CommunicationClient communicationClient = new CommunicationClient();


        System.out.println(communicationClient.authenticateUser("d", "es"));

        List<Book> list = communicationClient.getBookList();


    }

}
