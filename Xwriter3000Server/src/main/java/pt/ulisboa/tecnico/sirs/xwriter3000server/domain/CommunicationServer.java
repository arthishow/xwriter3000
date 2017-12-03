package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.databaseconnection.ConnectionDB;

import java.security.PublicKey;
import java.util.*;

public class CommunicationServer {

    private String symbols;

    private Random random;

    private ConnectionDB database;

    private List<ActiveUser> activeUsers;

    private CypherUtil cypherUtil;


    public CommunicationServer(CypherUtil cypherUtil){
        database = new ConnectionDB(cypherUtil);
        activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());
        symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
        random = new Random();
    }

    public Boolean createUser(String username, String password, String secret, String publicKey){
        Boolean success = database.createAuthor(username, password, secret, publicKey);
        return success;
    }

    public String authenticateUser(String username, String password){
        Boolean success = database.login(username, password);
        if (success){
            String publicKeyString = database.getPublicKey(username);
            PublicKey publicKey = cypherUtil.getPublicKeyFromString(publicKeyString);
            char[] sessionID = new char[20];
            for (int i = 0; i < 20; i++){
                sessionID[i] = symbols.toCharArray()[random.nextInt(symbols.toCharArray().length)];
            }
            ActiveUser user = new ActiveUser(new String(sessionID), username, publicKey);
            activeUsers.add(user);
            return new String(sessionID);
        }
        return null;
    }

    public int createBook(String sessionID, String title){
        for (ActiveUser activeUser : activeUsers) {
            if (sessionID.equals(activeUser.getSessionID())) {
                Book book = new Book(title);
                int bookID = database.createBook(book, activeUser.getUsername());
                return bookID;
            }
        }
        return -1;
    }

    public String sendBook(String sessionID, String bookID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                String bookContent = database.getBook(Integer.parseInt(bookID), activeUser.getUsername());
                return bookContent;
            }
        }
        return null;
    }

    //todo: implement this methods
    public Boolean receiveBookChanges(String sessionID, String bookID, String bookContent){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                Boolean result = database.changeBook(activeUser.getUsername(), Integer.parseInt(bookID), bookContent);
                return true;
            }
        }
        return false;
    }

    public List<Book> getBookList(String sessionID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                List<Book> bookList = database.getBookList(activeUser.getUsername());
                return bookList;
            }
        }
        return null;
    }


    public Boolean addAuthorAuth(String sessionID, String bookID, Map<String, Integer> authorsAuth){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID.equals(activeUser.getSessionID())){
                for (Map.Entry<String, Integer> authorAuth : authorsAuth.entrySet()){
                    database.addAuthorAuth(Integer.valueOf(bookID), activeUser.getUsername(), authorAuth.getKey(), authorAuth.getValue());
                }
                return true;

            }
        }
        return false;
    }

    public Boolean authorExists(String username) {
        return database.authorExists(username);
    }

    public List<String> getAuthorsFromBook(String sessionID, String bookID){
        for (ActiveUser activeUser : activeUsers) {
            if (sessionID.equals(activeUser.getSessionID())) {
                return database.getAuthorsFromBook(bookID, activeUser.getUsername());
            }
        }
        return null;
    }

    public Boolean logout(String sessionID, String username) {
        int removeIndex = -1;
        for (int i = 0; i < activeUsers.size(); i++) {
            if (sessionID.equals(activeUsers.get(i))) {
                removeIndex = i;
            }
        }
        if (removeIndex != -1){
            activeUsers.remove(removeIndex);
            return true;
        }
        return false;
    }

    //TODO: fix this method
    public Boolean forwardSymKey(){
        return true;
    }


}
