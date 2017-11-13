package xwriter3000;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String authorId;
    private String name;

    public User(String authorId, String name){
        this.authorId = authorId;
        this.name = name;
    }

    //TODO
    public List<Book> getBooks() {
        return Main.currentUserBooks;
    }

    //TODO
    public List<String> getBookTitles(){
        List<Book> books = getBooks();
        List<String> titles = new ArrayList<>();
        for(Book b: books){
            titles.add(b.getTitle());
        }
        return titles;
    }

    public String getName(){
        return name;
    }

}
