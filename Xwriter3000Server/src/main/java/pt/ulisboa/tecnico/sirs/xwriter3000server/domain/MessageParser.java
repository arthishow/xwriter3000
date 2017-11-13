package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    public Message parseType(Message message){
        String messageInside = message.getMessage();
        if (messageInside.startsWith("type:")){
            if(messageInside.indexOf("sendbook") == 5){
                message.setType("sendbook");
                message.setMessage(messageInside.substring(13));
                return message;
            }
            else if(messageInside.indexOf("receiveBookChanges") == 5){
                message.setType("receiveBookChanges");
                message.setMessage(messageInside.substring(23));
                return message;
            }
            else if(messageInside.indexOf("authenticateUser") == 5){
                message.setType("authenticateUser");
                message.setMessage(messageInside.substring(21));
                return message;
            }
            else if(messageInside.indexOf("forwardAssymKey") == 5){
                message.setType("forwardAssymKey");
                message.setMessage(messageInside.substring(20));
                return message;
            }
            //TODO: think this methods will be needed
            else if(messageInside.indexOf("register") == 5){
            }
            else if(messageInside.indexOf("getBookIDList") == 5){
                //TODO
            }
        }
        return null;
    }

    public List<String> parseAuthenticateUser(String message){
        String username;
        String password;
        if (message.startsWith("username:") && message.contains("password:")) {
            username = message.substring(9, message.indexOf("password:") - 1);
            password = message.substring(message.indexOf("password:") + 9).toString();
            List<String> credentials = new ArrayList<>();
            credentials.add(username);
            credentials.add(password);
            return credentials;
        }
        return null;
    }


    //untested
    public List<String> parseSendBook(String message){
        String bookID;
        String sessionID;
        if (message.startsWith("bookID:") && message.contains("sessionID:")) {
            bookID = message.substring(7, message.indexOf("sessionID:") - 1);
            sessionID = message.substring(message.indexOf("sessionID:") + 10).toString();
            List<String> bookInfo = new ArrayList<>();
            bookInfo.add(bookID);
            bookInfo.add(sessionID);
            return bookInfo;
        }
        return null;
    }

    //untested
    public List<String> parseReceiveBookChanges(String message){
        String book;
        String sessionID;
        if (message.startsWith("book:") && message.contains("sessionID:")){
            book = message.substring(5, message.indexOf("sessionID:") - 1);
            sessionID = message.substring(message.indexOf("sessionID:") + 10).toString();
            List<String> info = new ArrayList<>();
            info.add(book);
            info.add(sessionID);
            return info;
        }
        return null;
    }

}
