package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.ArrayList;
import java.util.List;

public class SelectBookController {

    protected static List<String> getBookTitles() {
        List<Book> bookList = Main.client.getBookList();
        List<String> titles = new ArrayList<>();
        for (Book b : bookList) {
            titles.add(b.getTitle());
        }
        return titles;
    }
}
