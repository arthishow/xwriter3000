package xwriter3000;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AccessAuthorization {

    private static int HEIGHT = 350;
    private static int WIDTH = 600;

    protected static void initAccessAuthorizationWindow(Stage stage){

        stage.setTitle("Manage books authorizations");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("Book title: ");
        grid.add(title, 0, 0);

        ComboBox comboBox = new ComboBox(FXCollections.observableArrayList(LoginController.user.getBookTitles()));
        comboBox.getSelectionModel().select(0);
        grid.add(comboBox, 1, 0);

        Text authorizedAuthors = new Text("Authors: ");
        grid.add(authorizedAuthors, 0, 1);

        ListView<String> authors = new ListView<>();
        //TODO
        //authors.setItems();
        grid.add(authors, 1, 1);

        Button addAuthor = new Button("Add author");
        grid.add(addAuthor, 0, 2);

        Button removeAuthor = new Button("Remove author");
        grid.add(removeAuthor, 1, 2);

        Button saveChanges = new Button("Save changes");
        grid.add(saveChanges, 2, 3);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 3, 3);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        saveChanges.setOnAction(e -> {
            List<User> users = new ArrayList<>();
            users.add(LoginController.user);

            for(String s : authors.getItems())
                users.add(WritingController.getUser(s));

            WritingController.setNewAuthorsForGivenBook(WritingController.currentBook.getTitle(), users);
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
