package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class Book {

    private int bookID;

    private String title;

    private String text;

    public Book(int bookID, String title, String text){
        this.bookID = bookID;
        this.title = title;
        this.text = text;
    }
}
