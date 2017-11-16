package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Book {

    private static final AtomicInteger count = new AtomicInteger(1);

    private int bookID;

    private String title;

    private String text;

    public Book(String title, String text){
        this.bookID = count.incrementAndGet();
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
