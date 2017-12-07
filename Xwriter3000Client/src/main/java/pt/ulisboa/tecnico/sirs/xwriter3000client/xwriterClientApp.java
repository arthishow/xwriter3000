package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000.Book;

import java.util.List;

//this main was just for testing the connection
public class xwriterClientApp {

    public static void main(String[] args) {
        CommunicationClient communicationClient = new CommunicationClient();


        System.out.println(communicationClient.authenticateUser("d", "es", false));

        System.out.println(communicationClient.getBook("13"));

        System.out.println(communicationClient.getBook("14"));

        List<Book> books = communicationClient.getBookList();

        for (Book book: books){
            System.out.println(book.getBookID());
            System.out.println(book.getTitle());
            System.out.println(book.getText());
        }

        //System.out.println(addAuthorsAuth(13, ));
    }

}
