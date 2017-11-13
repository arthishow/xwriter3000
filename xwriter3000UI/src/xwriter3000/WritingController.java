package xwriter3000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WritingController{

    protected static List<Book> books = LoginController.user.getBooks();
    protected static Book currentBook = books.get(1);

    //TODO
    protected static void sendBookChanges(String book) {

    }

    //TODO
    protected static void createBook(String title, List<User> authors){

    }

    //TODO
    protected static void setNewAuthorsForGivenBook(String title, List<User> authors){

    }

    //TODO
    protected static boolean authorExists(String userid){
        return true;
    }

    //TODO
    protected static User getUser(String userid){
        return null;
    }

}
