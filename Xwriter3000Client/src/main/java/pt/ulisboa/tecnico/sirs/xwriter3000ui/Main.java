package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.ulisboa.tecnico.sirs.xwriter3000client.CommunicationClient;

public class Main extends Application {

    protected static CommunicationClient client;

    /**
     * The starting function of the program. It will run the start() function automatically.
     * It is not expecting any program arguments.
     * @param args the program arguments
     */
    public static void main(String[] args) {
        client = new CommunicationClient();
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage){
        Login.initLogInWindow(primaryStage);
        primaryStage.show();
    }

    //TODO when the application is closed, it will run stop()
    @Override
    public void stop() {
        System.out.println("Stage is closing");
        //client.logout(null);
    }
}
