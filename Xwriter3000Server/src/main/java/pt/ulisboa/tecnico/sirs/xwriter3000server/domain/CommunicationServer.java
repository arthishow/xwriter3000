package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.databaseconnection.ConnectionDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunicationServer {

    private ConnectionDB database;

    private List<ActiveUser> activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());

    public CommunicationServer(){
        database = new ConnectionDB();
    }

    //todo: implement this methods
    public Boolean receiveBookChanges(String book, String sessionID){
        return true;
    }

    public String sendBook(String bookID, String sessionID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID == activeUser.getSessionID()){
                return "";
            }
        }
        return "Fail";
    }

    public Boolean createUser(String username, String password){
        Author author = new Author(username, password);
        Boolean success = database.createAuthor(author);
        return success;
    }

    public String authenticateUser(String username, String password){
        return "good sessionID";
    }

    //TODO: fix this method
    public Boolean forwardSymKey(){
        return true;
    }
}
