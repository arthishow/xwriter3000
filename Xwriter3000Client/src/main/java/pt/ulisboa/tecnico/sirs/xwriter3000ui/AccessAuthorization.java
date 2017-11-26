package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class AccessAuthorization {

    private static int HEIGHT = 350;
    private static int WIDTH = 600;

    static void initAccessAuthorizationWindow(Stage stage) {

        stage.setTitle("Manage books authorizations");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("Book title: ");
        grid.add(title, 0, 0);

        ComboBox<Book> comboBox = new ComboBox(FXCollections.observableArrayList(Main.client.getBookList()));
        comboBox.getSelectionModel().select(0);
        grid.add(comboBox, 1, 0);

        Text authorizedAuthors = new Text("Authors: ");
        grid.add(authorizedAuthors, 0, 1);

        ListView<String> authors = new ListView<>();
        if (comboBox.getSelectionModel().getSelectedItem() != null) {
            authors.getItems().addAll(Communication.getAuthorsFromGivenBook(comboBox.getSelectionModel().getSelectedItem().getBookID()));
        }
        grid.add(authors, 1, 1);

        Button addAuthor = new Button("Add author");
        grid.add(addAuthor, 0, 2);

        Button removeAuthor = new Button("Remove author");
        grid.add(removeAuthor, 1, 2);

        Button saveChanges = new Button("Save changes");
        grid.add(saveChanges, 2, 3);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 3, 3);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors, comboBox.getSelectionModel().getSelectedItem()));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        saveChanges.setOnAction(e -> {
            List<String> authorsId = new ArrayList<>();
            authorsId.addAll(authors.getItems());
            Communication.addAuthorsToGivenBook(authorsId, comboBox.getSelectionModel().getSelectedItem().getBookID());
            stage.close();
        });
        cancel.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
