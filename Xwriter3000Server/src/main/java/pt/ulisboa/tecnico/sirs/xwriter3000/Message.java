package pt.ulisboa.tecnico.sirs.xwriter3000;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 5950198465489512354L;

    private String type;

    private String message;

    private String signature;

    public Message(String message, String signature){
        this.message = message;
        this.signature = signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

