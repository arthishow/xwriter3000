package pt.ulisboa.tecnico.sirs.xwriter3000ui;

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
import pt.ulisboa.tecnico.sirs.xwriter3000.Book;
import pt.ulisboa.tecnico.sirs.xwriter3000.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AccessAuthorization {

    private static int HEIGHT = 350;
    private static int WIDTH = 630;

    /**
     * Generate and display the access authorization modification window.
     * When an author is added to the TableView, it is only added to the
     * database once the Save Changes button has been pressed.
     *
     * @param stage the container the window will own
     */
    protected static void initAccessAuthorizationWindow(Stage stage) {

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
        userIdCol.prefWidthProperty().bind(authors.widthProperty().multiply(0.8));
        levelCol.prefWidthProperty().bind(authors.widthProperty().multiply(0.2));
        userIdCol.setResizable(false);
        levelCol.setResizable(false);
        authors.setPlaceholder(new Label("No authors to display."));
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
        grid.add(actionText, 2, 4);

        addAuthor.setOnAction(e -> AddAuthor.initAddAuthorWindow(new Stage(), authors));
        removeAuthor.setOnAction(e -> authors.getItems().remove(authors.getSelectionModel().getSelectedItem()));
        saveChanges.setOnAction(e -> {
            String bookId = String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID());
            Map<String, Integer> authorsId = new HashMap<>();
            for (User user : authors.getItems()) {
                authorsId.put(user.getAuthorId(), user.getAuthorizationLevel());
            }
            if (AccessAuthorizationController.removeOldAuthorsFromGivenBook(bookId, authors.getItems()) &&
                    Main.client.addAuthorsAuth(bookId, authorsId)) {
                actionText.setFill(Color.GREEN);
                actionText.setText("Changes saved.");
            } else {
                actionText.setFill(Color.RED);
                actionText.setText("An error has occurred.");
            }
        });
        cancel.setOnAction(e -> stage.close());

        if (comboBox.getSelectionModel().getSelectedItem() != null) {
            authors.getItems().addAll(AccessAuthorizationController.createUserListFromGivenBook(String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID())));
        }
        comboBox.valueProperty().addListener(e -> {
            authors.getItems().clear();
            authors.getItems().addAll(AccessAuthorizationController.createUserListFromGivenBook(String.valueOf(comboBox.getSelectionModel().getSelectedItem().getBookID())));
        });

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
