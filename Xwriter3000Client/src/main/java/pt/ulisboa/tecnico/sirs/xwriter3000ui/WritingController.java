package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.stage.Stage;

class WritingController {

    //TODO implement logout

    /**
     * When logging-out, it displays the starting window (the log-in window).
     * @param stage the container the window will own
     */
    protected static void logout(Stage stage) {
        //Main.client.logout();
        Login.initLogInWindow(stage);
    }

}
