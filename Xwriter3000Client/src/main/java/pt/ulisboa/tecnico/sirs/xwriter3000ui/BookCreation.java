package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import pt.ulisboa.tecnico.sirs.xwriter3000.User;

import java.util.HashMap;
import java.util.Map;

public class BookCreation {

    private static int HEIGHT = 350;
    private static int WIDTH = 600;

    /**
     * Generate and display a book creation Window.
     *
     * @param stage the container the window will own
     */
    protected static void initBookCreationWindow(Stage stage) {

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

        TableView<User> authors = new TableView<>();
        authors.setEditable(false);
        TableColumn userIdCol = new TableColumn("User ID");
        userIdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> data) {
                return new ReadOnlyStringWrapper(data.getValue().getAuthorId());
            }
        });
        TableColumn levelCol = new TableColumn("Level");
        levelCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> data) {
                return new ReadOnlyStringWrapper(String.valueOf(data.getValue().getAuthorizationLevel()));
            }
        });
        authors.getColumns().addAll(userIdCol, levelCol);
        userIdCol.prefWidthProperty().bind(authors.widthProperty().multiply(0.7));
        levelCol.prefWidthProperty().bind(authors.widthProperty().multiply(0.3));
        userIdCol.setResizable(false);
        levelCol.setResizable(false);
        authors.setPlaceholder(new Label("No authors to display."));
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
        grid.add(actionText, 2, 4);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        createBook.setOnAction(e -> {
            Map<String, Integer> authorsId = new HashMap<>();
            for (User user : authors.getItems()) {
                authorsId.put(user.getAuthorId().toString(), user.getAuthorizationLevel());
            }
            if (Main.client.createBook(title.getText(), authorsId) >= 0) {
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
