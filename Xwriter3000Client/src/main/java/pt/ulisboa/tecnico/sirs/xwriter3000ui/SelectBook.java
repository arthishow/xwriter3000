package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pt.ulisboa.tecnico.sirs.xwriter3000.Book;

class SelectBook {

    private static int HEIGHT = 600;
    private static int WIDTH = 500;

    /**
     * Generate and display a window that allows the user to see the
     * books he owns, to manage them, access options, log-out, and select
     * the book he wants to work on.
     * @param stage the container the window will own
     */
    protected static void initSelectBookWindow(Stage stage) {

        stage.setTitle("Xwriter 3000 - Book selection");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text books = new Text("Books: ");
        books.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(books, 0, 0);

        ListView<Book> bookList = new ListView<>();
        bookList.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);

                if (empty || book == null || book.getTitle() == null) {
                    setText(null);
                } else {
                    setText(book.getTitle());
                }
            }
        });
        bookList.setPrefHeight(450);
        bookList.setPrefWidth(400);
        grid.add(bookList, 0, 1);

        Button createBook = new Button("Create book");

        Button manageAuthorizations = new Button("Manage authorizations");

        Button selectBook = new Button("Select book");

        Button refresh = new Button("Refresh");

        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(createBook);
        hbBtn1.getChildren().add(manageAuthorizations);
        hbBtn1.getChildren().add(refresh);
        hbBtn1.getChildren().add(selectBook);
        grid.add(hbBtn1, 0, 2);

        Button options = new Button("Options");
        Button logout = new Button("Log-out");

        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn2.getChildren().addAll(options, logout);
        grid.add(hbBtn2, 0, 3);

        createBook.setOnAction(e -> BookCreation.initBookCreationWindow(new Stage()));
        manageAuthorizations.setOnAction(e -> {
            if (!bookList.getItems().isEmpty()) {
                AccessAuthorization.initAccessAuthorizationWindow(new Stage());
            } else {
                PopupMessage.initPopupMessageWindow(new Stage(), "Warning",
                        "No books to manage.", 100, 75);
            }
        });
        refresh.setOnAction(e -> {
            bookList.getItems().clear();
            bookList.getItems().addAll(Main.client.getBookList());
        });
        selectBook.setOnAction(e -> {
            if (bookList.getSelectionModel().getSelectedItem() != null) {
                Writing.initTextEditingWindow(stage, bookList.getSelectionModel().getSelectedItem());
            } else {
                PopupMessage.initPopupMessageWindow(new Stage(), "Warning",
                        "No book selected.", 100, 75);
            }
        });

        options.setOnAction(e -> Options.initOptionsWindow(new Stage()));
        logout.setOnAction(e -> {
            Main.client.logout();
            Login.initLogInWindow(stage);
        });

        bookList.getItems().addAll(Main.client.getBookList());

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
