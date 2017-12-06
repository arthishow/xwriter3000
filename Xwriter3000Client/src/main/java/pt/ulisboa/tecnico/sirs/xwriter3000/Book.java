package pt.ulisboa.tecnico.sirs.xwriter3000;


/**
 * A class that defines a Book object.
 * Made to make the displaying of book information easier in the UI.
 */
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

    public String getText(){
        return text;
    }

    @Override
    public String toString() {
        return title;
    }

}
