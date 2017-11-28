package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        ComboBox<Book> comboBox = new ComboBox<>();
        List<Book> books = Main.client.getBookList();
        comboBox.getItems().addAll(books);
        comboBox.getSelectionModel().select(0);
        grid.add(comboBox, 1, 0);

        Text authorizedAuthors = new Text("Authors: ");
        grid.add(authorizedAuthors, 0, 1);

        TableView<User> authors = new TableView<>();
        if (comboBox.getSelectionModel().getSelectedItem() != null) {
            authors.getItems().addAll(AccessAuthorizationController.createUserListFromGivenBook(String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID())));
        }
        comboBox.valueProperty().addListener(e -> {
            authors.getItems().removeAll();
            authors.getItems().addAll(AccessAuthorizationController.createUserListFromGivenBook(String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID())));
        });
        grid.add(authors, 1, 1);

        Button addAuthor = new Button("Add author");
        grid.add(addAuthor, 0, 2);

        Button removeAuthor = new Button("Remove author");
        grid.add(removeAuthor, 1, 2);

        Button saveChanges = new Button("Save changes");
        grid.add(saveChanges, 2, 3);

        Button cancel = new Button("Cancel");
        grid.add(cancel, 3, 3);

        Text actionText = new Text();
        grid.add(actionText, 3, 4);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        saveChanges.setOnAction(e -> {
            List<String> authorsId = new ArrayList<>();
            for(User user: authors.getItems()){
                authorsId.add(user.getAuthorId());
            }
            if(Main.client.addAuthorsAuth(String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID()), authorsId)){
                actionText.setFill(Color.GREEN);
                actionText.setText("Changes saved.");
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(e2 -> stage.close());
                delay.play();
            }else{
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
