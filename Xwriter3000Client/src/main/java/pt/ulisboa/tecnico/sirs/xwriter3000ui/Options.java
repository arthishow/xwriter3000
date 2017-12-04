package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Options {

    private static int HEIGHT = 300;
    private static int WIDTH = 250;

    static void initOptionsWindow(Stage stage) {

        stage.setTitle("Options");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button displayCode = new Button("Display personal code");
        grid.add(displayCode, 0 ,0);

        displayCode.setOnAction(e -> Popup.initPopupWindow(new Stage(), "Your personal code is\n" + OptionsController.getPersonalCode() ));
    }
}
