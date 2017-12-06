package pt.ulisboa.tecnico.sirs.xwriter3000ui;


public class Book {

    private int bookID;
    private String title;
    private String text;

    public Book(int bookID, String title) {
        this.bookID = bookID;
        this.title = title;
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
