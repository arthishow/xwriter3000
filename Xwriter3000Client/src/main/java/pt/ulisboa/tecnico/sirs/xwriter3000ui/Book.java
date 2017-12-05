package pt.ulisboa.tecnico.sirs.xwriter3000ui;


public class Book {

    private int bookID;
    private String title;
    private String text;

    public Book(int bookID, String title){
        this.bookID = bookID;
        this.title = title;
    }

    public Book(String title, String text){
        this.title = title;
        this.text = text;
    }

    public Book(int bookID, String title, String text){
        this.bookID = bookID;
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

    @Override
    public String toString() {
        return title;
    }

}
