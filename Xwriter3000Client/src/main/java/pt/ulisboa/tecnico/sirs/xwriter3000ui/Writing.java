package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        MenuItem saveBook = new MenuItem("Save to the cloud");
        MenuItem manageBooks = new MenuItem("Manage books");

        //Edit Menu
        Menu menuEdit = new Menu("Edit");
        MenuItem authorizations = new MenuItem("Manage authorizations");

        //User Menu
        Menu menuUser = new Menu("User");
        MenuItem logout = new MenuItem("Log-out");

        TextArea text = new TextArea();
        text.setWrapText(true);
        if (book == null) {
            text.setText("Once upon a time...");
        } else {
            currentBook = book;
            text.setText(Main.client.getBook(String.valueOf(currentBook.getBookID())));
        }

        manageBooks.setOnAction(e -> {
            PopupChoice window = new PopupChoice();
            window.initPopupChoiceWindow(new Stage(), "Warning",
                    "Are you sure you want to leave?\nChanges will not be saved.", 110, 275);
            if (window.getChoice()) {
                SelectBook.initSelectBookWindow(stage);
            }
        });
        saveBook.setOnAction(e -> {
            if (Main.client.sendBookChanges(String.valueOf(currentBook.getBookID()), text.getText())) {
                PopupMessage.initPopupMessageWindow(new Stage(), "Info", "Changes saved",
                        100, 70);
            } else {
                PopupMessage.initPopupMessageWindow(new Stage(), "Warning",
                        "An error occured:\nChanges weren't saved.", 100, 75);
            }
        });
        authorizations.setOnAction(e -> AccessAuthorization.initAccessAuthorizationWindow(new Stage()));
        logout.setOnAction(e -> {
            PopupChoice window = new PopupChoice();
            window.initPopupChoiceWindow(new Stage(), "Warning",
                    "Are you sure you want to log-out?\nChanges will not be saved.", 110, 275);
            if (window.getChoice()) {
                WritingController.logout(stage);
            }
        });

        //Menus
        menuFile.getItems().addAll(saveBook, manageBooks);
        menuEdit.getItems().addAll(authorizations);
        menuUser.getItems().addAll(logout);
        menuBar.getMenus().addAll(menuFile, menuEdit, menuUser);

        Label statusBar = new Label("Working on " + currentBook.getTitle());
        statusBar.setFont(Font.font(11));

        text.setPrefHeight(HEIGHT - 30);
        text.setPrefWidth(WIDTH);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, text, statusBar);
        stage.setResizable(false);
        stage.setScene(scene);
    }
}
