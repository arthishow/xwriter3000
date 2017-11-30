package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.Date;

public class ActiveUser {

    private String sessionID;

    private String username;

    private Date timestamp;

    //README: this class might need something like the ip from the user

    public ActiveUser(String sessionID, String username) {
        this.sessionID = sessionID;
        this.username = username;
        timestamp = new Date();
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUsername() {
        return username;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
