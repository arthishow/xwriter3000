package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.stage.Stage;

import java.util.List;

public class WritingController{

    protected static void logout(Stage stage) {
        //Main.client.logout();
        Login.initLogInWindow(stage);
    }

}
