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
        this.cypherUtil = cypherUtil;
        random = new Random();
    }

    public Boolean createUser(String username, String password, String secret, String publicKey){
        Boolean success = database.createAuthor(username, password, secret, publicKey);
        return success;
    }

    public ActiveUser authenticateUser(String username, String password){
        Boolean success = database.login(username, password);
        if (success){
            String publicKeyString = database.getPublicKey(username);
            PublicKey publicKey = cypherUtil.getPublicKeyFromString(publicKeyString);
            char[] sessionID = new char[20];
            for (int i = 0; i < 20; i++){
                sessionID[i] = symbols.toCharArray()[random.nextInt(symbols.toCharArray().length)];
            }
            ActiveUser activeUser = new ActiveUser(new String(sessionID), username, publicKey);
            activeUsers.add(activeUser);
            return activeUser;
        }
        return null;
    }

    public int createBook(ActiveUser activeUser, String title){
        Book book = new Book(title);
        int bookID = database.createBook(book, activeUser.getUsername());
        return bookID;
    }

    public String sendBook(ActiveUser activeUser, String bookID){
        String bookContent = database.getBook(Integer.parseInt(bookID), activeUser.getUsername());
        return bookContent;
    }


    public Boolean receiveBookChanges(ActiveUser activeUser, String bookID, String bookContent){
        Boolean result = database.changeBook(activeUser.getUsername(), Integer.parseInt(bookID), bookContent);
        return result;
    }

    public List<Book> getBookList(ActiveUser activeUser){
        List<Book> bookList = database.getBookList(activeUser.getUsername());
        return bookList;
    }


    public Boolean addAuthorAuth(ActiveUser activeUser, String bookID, Map<String, Integer> authorsAuth){
        Boolean success = true;
        for (Map.Entry<String, Integer> authorAuth : authorsAuth.entrySet()){
            if(!database.addAuthorAuth(Integer.valueOf(bookID), activeUser.getUsername(), authorAuth.getKey(), authorAuth.getValue())){
                success = false;
            }
        }
        return success;
    }

    public Boolean authorExists(String username) {
        return database.authorExists(username);
    }

    public List<String> getAuthorsFromBook(ActiveUser activeUser, String bookID){
        return database.getAuthorsFromBook(bookID, activeUser.getUsername());
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


    public ActiveUser activeUser(String sessionID){
        for (ActiveUser activeUser : activeUsers) {
            if (sessionID.equals(activeUser.getSessionID())) {
                return activeUser;
            }
        }
        return null;
    }

}
