package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Writing {

    private static int HEIGHT = 600;
    private static int WIDTH = 500;

    private static Book currentBook;

    protected static void initTextEditingWindow(Stage stage, Book book) {

        stage.setTitle("Xwriter 3000");
        Scene scene = new Scene(new VBox(), WIDTH, HEIGHT);

        MenuBar menuBar = new MenuBar();

        //File Menu
        Menu menuFile = new Menu("File");
        MenuItem createBook = new MenuItem("Create book...");
        MenuItem saveBook = new MenuItem("Save to the cloud");
        MenuItem selectBook = new Menu("Select book");

        //Edit Menu
        Menu menuEdit = new Menu("Edit");
        MenuItem access = new MenuItem("Manage access level");

        //User Menu
        Menu menuUser = new Menu("User");
        MenuItem logout = new MenuItem("Log-out");

        TextArea text = new TextArea();
        if (book == null) {
            text.setText("Once upon a time...");
        } else {
            currentBook = book;
            text.setText(book.getText());
        }

        selectBook.setOnAction(e -> {
            Main.client.sendBookChanges(String.valueOf(currentBook.getBookID()), text.getText());
            SelectBook.initSelectBookWindow(stage);
        });
        createBook.setOnAction(e -> BookCreation.initBookCreationWindow(new Stage()));
        saveBook.setOnAction(e -> Main.client.sendBookChanges(String.valueOf(currentBook.getBookID()), currentBook.getText()));
        access.setOnAction(e -> AccessAuthorization.initAccessAuthorizationWindow(new Stage()));
        logout.setOnAction(e -> WritingController.logout(stage));

        //Menus
        menuFile.getItems().addAll(createBook, saveBook, selectBook);
        menuEdit.getItems().addAll(access);
        menuUser.getItems().addAll(logout);
        menuBar.getMenus().addAll(menuFile, menuEdit, menuUser);

        text.setPrefHeight(HEIGHT-30);
        text.setPrefWidth(WIDTH);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, text);
        stage.setResizable(false);
        stage.setScene(scene);
    }
}
