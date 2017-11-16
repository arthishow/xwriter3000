package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunicationServer {

    private List<ActiveUser> activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());

    public CommunicationServer(){
    }

    //todo: implement this methods
    public boolean receiveBookChanges(String book, String sessionID){
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

    public String authenticateUser(String username, String password){
        return "good sessionID";
    }

    //TODO: fix this method
    public Boolean forwardSymKey(){
        return true;
    }
}
