package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000ui.Book;

import java.util.List;

//this main was just for testing the connection
public class xwriterClientApp {

    public static void main(String[] args) {
        CommunicationClient communicationClient = new CommunicationClient();


        System.out.println(communicationClient.authenticateUser("test", "goodPass"));

        List<Book> list = communicationClient.getBookList();

        System.out.println(list.get(0).getBookID());

        System.out.println(list.get(0).getTitle());

        System.out.println(list.get(1).getBookID());

        System.out.println(list.get(1).getTitle());

    }

}
