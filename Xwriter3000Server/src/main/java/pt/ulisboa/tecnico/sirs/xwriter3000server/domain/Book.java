package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Book {

    private int bookID;
    private static final AtomicInteger count = new AtomicInteger(0);
    private String title;
    private String text;

    public Book(int bookID, String title) {
        this.bookID = bookID;
        this.title = title;
    }

    public Book(String title) {
        this.title = title;
    }

    public Book(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
