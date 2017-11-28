package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.databaseconnection.ConnectionDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CommunicationServer {

    private String symbols;

    private Random random;

    private ConnectionDB database;

    private List<ActiveUser> activeUsers;


    public CommunicationServer(){
        database = new ConnectionDB();
        activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());
        symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
        random = new Random();
    }

    public Boolean createUser(String username, String password){
        Boolean success = database.createAuthor(username, password);
        return success;
    }

    public String authenticateUser(String username, String password){
        int authorID = database.login(username, password);
        if (authorID != -1){
            char[] sessionID = new char[20];
            for (int i = 0; i < 20; i++){
                sessionID[i] = symbols.toCharArray()[random.nextInt(symbols.toCharArray().length)];
            }
            ActiveUser user = new ActiveUser(new String(sessionID), authorID);
            activeUsers.add(user);
            return new String(sessionID);
        }
        return null;
    }

    public Boolean createBook(String sessionID, String title){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                Book book = new Book(title);
                return database.createBook(book, activeUser.getAuthorID());
            }
        }
        return false;
    }

    public String sendBook(String sessionID, String bookID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                String bookContent = database.getBook(Integer.parseInt(bookID), activeUser.getAuthorID());
                return bookContent;
            }
        }
        return null;
    }

    //todo: implement this methods
    public Boolean receiveBookChanges(String sessionID, String bookID, String bookContent){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                Boolean result = database.changeBook(activeUser.getAuthorID(), Integer.parseInt(bookID), bookContent);
                return true;
            }
        }
        return false;
    }

    public List<Book> getBookList(String sessionID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                List<Book> bookList = database.getBookList(activeUser.getAuthorID());
                return bookList;
            }
        }
        return null;
    }


    public Boolean addAuthorAuth(String sessionID, String bookID, List<String> authorIDs){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                for(String authorID: authorIDs){
                    database.addAuthorAuth(Integer.valueOf(bookID), Integer.valueOf(authorID));
                }

            }
        }
        return false;
    }

    public Boolean authorExists(String sessionID, String username) {
        for (ActiveUser activeUser : activeUsers) {
            if (sessionID.equals(activeUser.getSessionID())) {
                return database.authorExists(username);
            }
        }
        return false;
    }

    public List<String> getAuthorsFromBook(String sessionID, String bookID){
        for (ActiveUser activeUser : activeUsers) {
            if (sessionID.equals(activeUser.getSessionID())) {
                return database.getAuthorsFromBook(bookID);
            }
        }
        return null;
    }

    //TODO: fix this method
    public Boolean forwardSymKey(){
        return true;
    }


}
