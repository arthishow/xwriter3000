package xwriter3000;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application{

    protected static User currentUser;
    protected static List<Book> currentUserBooks = new ArrayList<>();

    public static void main(String[] args) {
        currentUser = new User("abc12345", "Assa");
        Book a = new Book("yo part 1", currentUser);
        Book b = new Book("yo part 2", currentUser);
        currentUserBooks.add(a);
        currentUserBooks.add(b);

        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Login.initLogInWindow(primaryStage);
        primaryStage.show();
    }
}
