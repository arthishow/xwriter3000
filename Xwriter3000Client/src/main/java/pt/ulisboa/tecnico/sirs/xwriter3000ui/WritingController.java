package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.List;

public class WritingController{

    protected static List<Book> books = LoginController.user.getBooks();
    protected static Book currentBook = books.get(1);

    //protected static void getBookLists(){
    //    books = Main.client.getBookList();
    //}

    //TODO
    protected static void sendBookChanges(String book) {
        //FIXME needs bookID
        //Main.client.sendBookChanges(bookId ,book);
    }

    //TODO
    protected static void createBook(String title, List<User> authors){

    }

    //TODO
    protected static void setNewAuthorsForGivenBook(String title, List<User> authors){

    }

    //TODO
    protected static boolean authorExists(String userid){
        return false;
    }

    //TODO
    protected static User getUser(String userid){
        return null;
    }

}
