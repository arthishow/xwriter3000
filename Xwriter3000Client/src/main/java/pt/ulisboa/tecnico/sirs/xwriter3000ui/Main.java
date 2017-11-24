package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.ulisboa.tecnico.sirs.xwriter3000client.CommunicationClient;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application{

    protected static User currentUser;
    protected static List<Book> currentUserBooks = new ArrayList<>();

    protected static CommunicationClient client;


    public static void main(String[] args) {
        currentUser = new User("abc12345", "Assa");
        client = new CommunicationClient();
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Login.initLogInWindow(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("Stage is closing");
        //client.logout(null);
    }
}
