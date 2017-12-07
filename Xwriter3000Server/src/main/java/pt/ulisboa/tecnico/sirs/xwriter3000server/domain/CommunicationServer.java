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

    public int createBook(ActiveUser activeUser, String title, String secretKey){
        int bookID = database.createBook(title, activeUser.getUsername(), secretKey);
        return bookID;
    }

    public String sendBook(ActiveUser activeUser, String bookID){
        String bookContent = database.getBook(Integer.valueOf(bookID), activeUser.getUsername());

        String tempKey = database.getTempKey(activeUser.getUsername(), Integer.valueOf(bookID));


        if(tempKey == null){
            String key = database.getSecretKey(activeUser.getUsername(), Integer.valueOf(bookID));
            String message = "sendBook:" + bookContent + "key:" + key;
            return message;
        }
        else{
            String message = "sendBook:" + bookContent + "tempKey:" + tempKey;
            return message;
        }
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
            Boolean temp = database.addAuthorAuth(Integer.valueOf(bookID), activeUser.getUsername(), authorAuth.getKey(), authorAuth.getValue());
            if(!temp){
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


    public String getSecretKey(String username, String bookId){
        return database.getSecretKey(username, Integer.valueOf(bookId));
    }

    public String getPublicKey(String username){
        return database.getPublicKey(username);
    }

    public String getPrivateKey(String username){
        return database.getPrivateKey(username);
    }

    public Boolean logout(String sessionID, String username) {
        int removeIndex = -1;
        for (int i = 0; i < activeUsers.size(); i++) {
            if (sessionID.equals(activeUsers.get(i).getSessionID())) {
                removeIndex = i;
            }
        }
        if (removeIndex != -1){
            activeUsers.remove(removeIndex);
            return true;
        }
        return false;
    }

    public void storeTempKey(String username, String bookID, String tempKey){
        database.storeTempKey(username, Integer.valueOf(bookID), tempKey);
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

    public void setSecretKey(String username, String bookID, String symKey){
        database.setSecretKey(username, Integer.valueOf(bookID), symKey);
    }

    public void updateSecretKey(String username, String bookID, String symKey){
        database.updateSecretKey(username, Integer.valueOf(bookID), symKey);
    }

    public Boolean removeUser(String bookID, String remAuthor){
        return database.removeUser(Integer.valueOf(bookID), remAuthor);
    }

    public Boolean removeTempKey(String bookID, String remAuthor){
        return database.removeTempKey(Integer.valueOf(bookID), remAuthor);
    }

    public Boolean removeSymKey(String bookID, String remAuthor){
        return database.removeSymKey(Integer.valueOf(bookID), remAuthor);
    }

    public Boolean checkTempKey(String bookID, String remAuthor){
        return database.checkTempKey(Integer.valueOf(bookID), remAuthor);
    }

    public Boolean checkSymKey(String bookID, String remAuthor){
        return database.checkSymKey(Integer.valueOf(bookID), remAuthor);
    }

    public Integer getAuthFromBook(String bookID, String username){
        return database.getAuthFromBook(Integer.valueOf(bookID), username);
    }


}
