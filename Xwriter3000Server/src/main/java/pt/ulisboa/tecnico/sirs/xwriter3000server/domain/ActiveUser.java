package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.Date;

public class ActiveUser {

    private String sessionID;

    private int authorID;

    private Date timestamp;

    //README: this class might need something like the ip from the user

    public ActiveUser(String sessionID, int authorID) {
        this.sessionID = sessionID;
        this.authorID = authorID;
        timestamp = new Date();
    }

    public String getSessionID() {
        return sessionID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
