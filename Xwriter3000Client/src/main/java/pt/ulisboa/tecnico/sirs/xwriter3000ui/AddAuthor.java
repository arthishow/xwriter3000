package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class AddAuthor {

    private static int HEIGHT = 130;
    private static int WIDTH = 330;

    static void initAddAuthorWindow(Stage stage, TableView<User> authors) {

        stage.setTitle("Add author");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text userId = new Text("User ID: ");
        grid.add(userId, 0, 0);

        TextField author = new TextField();
        grid.add(author, 1, 0);

        Text actionText = new Text();
        grid.add(actionText, 2, 0);

        ToggleGroup group = new ToggleGroup();
        RadioButton read = new RadioButton("Read");
        read.setToggleGroup(group);
        read.setUserData(2);
        read.setSelected(true);
        RadioButton readAndWrite = new RadioButton("Read&Write");
        readAndWrite.setToggleGroup(group);
        readAndWrite.setUserData(1);
        grid.add(read, 0, 1);
        grid.add(readAndWrite, 1, 1);

        Button addAuthor = new Button("Add");
        grid.add(addAuthor, 0, 2);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 1, 2);

        addAuthor.setOnAction(e -> {
            if (Main.client.authorExists(author.getText()) && !author.getText().equals(Login.currentUserId) && !authors.getItems().contains(author.getText())) {
                int authLvl = (int) group.getSelectedToggle().getUserData();
                authors.getItems().add(new User(author.getText(), authLvl));
                actionText.setFill(Color.GREEN);
                actionText.setText(author.getText() + " added.");
                author.setText("");
            }else{
                actionText.setFill(Color.RED);
                actionText.setText("Incorrect User ID.");
            }
        });

        cancel.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
