package xwriter3000;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

public class Writing {

    private static int HEIGHT = 600;
    private static int WIDTH = 500;

    protected static void initTextEditingWindow(Stage stage){

        stage.setTitle("Xwriter 3000");
        stage.setResizable(true);
        Scene scene = new Scene(new VBox(), WIDTH, HEIGHT);

        MenuBar menuBar = new MenuBar();

        //File Menu
        Menu menuFile = new Menu("File");
        MenuItem createBook = new MenuItem("Create book...");
        MenuItem saveBook = new MenuItem("Save to the cloud");
        Menu menuBooks = new Menu("Work on ...");

        //Edit Menu
        Menu menuEdit = new Menu("Edit");
        MenuItem access = new MenuItem("Manage access level");

        TextArea text = new TextArea("Once upon a time...");

        for (Book book: LoginController.user.getBooks()) {
            MenuItem b = new MenuItem(book.getTitle());
            b.setOnAction(e -> {
                String t = book.getText();
                text.setText(t);
            });
            menuBooks.getItems().add(b);
        }

        createBook.setOnAction(e -> BookCreation.initBookCreationWindow(new Stage()));
        saveBook.setOnAction(e -> WritingController.sendBookChanges(text.getText()));

        access.setOnAction(e -> AccessAuthorization.initAccessAuthorizationWindow(new Stage()));

        //Menus
        menuFile.getItems().addAll(createBook, saveBook, menuBooks);
        menuEdit.getItems().addAll(access);
        menuBar.getMenus().addAll(menuFile, menuEdit);

        text.setPrefHeight(HEIGHT-30);
        text.setPrefWidth(WIDTH);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, text);
        stage.setResizable(false);
        stage.setScene(scene);
    }
}
