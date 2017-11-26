package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class AddAuthor {

    private static int HEIGHT = 100;
    private static int WIDTH = 330;

    static void initAddAuthorWindow(Stage stage, ListView<String> authors, Book book) {

        stage.setTitle("Add author");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text userid = new Text("User ID: ");
        grid.add(userid, 0, 0);

        TextField author = new TextField();
        grid.add(author, 1, 0);

        Text actionText = new Text();
        grid.add(actionText, 2, 0);

        Button addAuthor = new Button("Add");
        grid.add(addAuthor, 0, 1);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 1, 1);

        addAuthor.setOnAction(e -> {
            if (Communication.authorExists(author.getText())) {
                if (book != null) {
                    List<String> authorList = new ArrayList<>();
                    authorList.add(author.getText());
                    Communication.addAuthorsToGivenBook(authorList, book.getBookID());
                }
                authors.getItems().add(author.getText());
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
