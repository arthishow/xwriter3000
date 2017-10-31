package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.Date;
import java.text.SimpleDateFormat;

public class ActiveUser {

    private String sessionID;

    private int authorID;

    private Date timestamp;

    public ActiveUser(String sessionID, int authorID) {
        this.sessionID = sessionID;
        this.authorID = authorID;
        timestamp = new Date();
    }

}
