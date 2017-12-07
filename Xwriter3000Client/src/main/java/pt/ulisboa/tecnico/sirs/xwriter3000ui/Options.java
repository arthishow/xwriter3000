package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.ulisboa.tecnico.sirs.xwriter3000client.CypherUtil;

class Options {

    private static int HEIGHT = 150;
    private static int WIDTH = 225;

    /**
     * Generate and display a window with options the user has.
     * For now it only allows the user to see his personal code
     * that is stored on his machine.
     *
     * @param stage the container the window will own
     */
    protected static void initOptionsWindow(Stage stage) {

        stage.setTitle("Options");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button displayCode = new Button("Display personal code");
        grid.add(displayCode, 0, 0);

        displayCode.setOnAction(e -> PopupMessage.initPopupMessageWindow(new Stage(), "Info",
                "Your personal code is\n" + CypherUtil.readSalt(Login.currentUserId)
                        + "\nYou will need it to log-in on another machine.",
                125, 120));

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
