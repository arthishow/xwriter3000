package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.ulisboa.tecnico.sirs.xwriter3000client.CommunicationClient;


public class Main extends Application {

    static CommunicationClient client;

    public static void main(String[] args) {
        client = new CommunicationClient();
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Login.initLogInWindow(primaryStage);
        primaryStage.show();
    }

    //TODO
    @Override
    public void stop() {
        System.out.println("Stage is closing");
        //client.logout(null);
    }
}
