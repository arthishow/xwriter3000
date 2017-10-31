package pt.tecnico.ulisboa.sirs.xwriter3000client;

public interface IComunicationClient {

    public void sendBookChanges(String bookText);

    public String getBook(String title, int bookID);

    public void login(String username, String password);

}
