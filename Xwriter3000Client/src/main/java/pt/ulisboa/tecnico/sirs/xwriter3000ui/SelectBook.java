package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import com.sun.org.apache.bcel.internal.generic.Select;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectBook {

    private static int HEIGHT = 600;
    private static int WIDTH = 500;

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

        Button addAuthor = new Button("Add author");

        Button selectBook = new Button("Select book");

        Button refresh = new Button("Refresh");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(createBook);
        hbBtn.getChildren().add(addAuthor);
        hbBtn.getChildren().add(refresh);
        hbBtn.getChildren().add(selectBook);
        grid.add(hbBtn, 0, 2);

        createBook.setOnAction(e -> BookCreation.initBookCreationWindow(new Stage()));
        addAuthor.setOnAction(e -> {
            ListView<String> authors = new ListView<>();
            Book book = bookList.getSelectionModel().getSelectedItem();
            authors.getItems().addAll(Communication.getAuthorsFromGivenBook(book.getBookID()));
            AddAuthor.initAddAuthorWindow(new Stage(), authors, book);
        });
        refresh.setOnAction(e -> {
            bookList.getItems().removeAll();
            bookList.getItems().addAll(Main.client.getBookList());
        });
        selectBook.setOnAction(e -> Writing.initTextEditingWindow(stage, bookList.getSelectionModel().getSelectedItem()));

        bookList.getItems().addAll(Main.client.getBookList());

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
