package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class BookCreation {

    private static int HEIGHT = 350;
    private static int WIDTH = 600;

    protected static void initBookCreationWindow(Stage stage){

        stage.setTitle("Create book");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text giveTitle = new Text("Book title: ");
        grid.add(giveTitle, 0, 0);

        TextField title = new TextField();
        grid.add(title, 1, 0);

        Text authorizedAuthors = new Text("Added authors: ");
        grid.add(authorizedAuthors, 0, 1);

        ListView<String> authors = new ListView<>();
        grid.add(authors, 1, 1);

        Button addAuthor = new Button("Add author");
        grid.add(addAuthor, 0, 2);

        Button removeAuthor = new Button("Remove author");
        grid.add(removeAuthor, 1, 2);

        Button createBook = new Button("Create book");
        grid.add(createBook, 2, 3);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 3, 3);

        Text actionText = new Text();
        grid.add(actionText, 4, 3);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors, null));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        createBook.setOnAction(e -> {
            List<String> authorsId = new ArrayList<>();
            authorsId.addAll(authors.getItems());
            if (Main.client.createBook(title.getText(), authors)) {
                actionText.setFill(Color.GREEN);
                actionText.setText("Book created.");
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(e2 -> stage.close());
                delay.play();
            } else {
                actionText.setFill(Color.RED);
                actionText.setText("An error has occurred.");
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
