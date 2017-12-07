package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.security.PublicKey;
import java.util.Date;

public class ActiveUser {

    private String sessionID;
    private String username;
    private PublicKey publicKey;
    private Date timestamp;

    public ActiveUser(String sessionID, String username) {
        this.sessionID = sessionID;
        this.username = username;
        timestamp = new Date();
    }

    public ActiveUser(String sessionID, String username, PublicKey publicKey) {
        this.sessionID = sessionID;
        this.username = username;
        this.publicKey = publicKey;
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
