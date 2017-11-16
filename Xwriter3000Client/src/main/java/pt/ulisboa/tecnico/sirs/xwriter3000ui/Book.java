package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Book {

    private String title;
    private int bookId;
    private List<User> authors;

    public Book(String title, User author){
        this.title = title;
        this.bookId = new Random().nextInt();
        this.authors = new ArrayList<>();
        this.authors.add(author);
    }

    public String getTitle(){
        return this.title;
    }

    //TODO
    private void addAuthor(User author){
        this.authors.add(author);
    }

    //TODO
    protected String getText(){
        return "I'm the book you need.";
    }

}
