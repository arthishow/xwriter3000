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
            else if(messageInside.indexOf("createUser") == 5){
                message.setType("createUser");
                message.setMessage(messageInside.substring(15));
                return message;
            }
            else if(messageInside.indexOf("getBookList") == 5){
                message.setType("getBookList");
                message.setMessage(messageInside.substring(16));
                return message;
            }
            else if(messageInside.indexOf("createBook") == 5){
                message.setType("createBook");
                message.setMessage(messageInside.substring(15));
                return message;
            }
        }
        return null;
    }

    public List<String> parseUserInfo(String message){
        String username;
        String password;
        if (message.startsWith("username:") && message.contains("password:")) {
            username = message.substring(9, message.indexOf("password:"));
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
            bookID = message.substring(7, message.indexOf("sessionID:"));
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
        String sessionID;
        String bookID;
        String bookContent;
        if (message.startsWith("sessionID:") && message.contains("bookID:") && message.contains("bookContent:")){
            sessionID = message.substring(9, message.indexOf("bookID:"));
            message = message.substring(message.indexOf("bookID:"));
            bookID = message.substring(5, message.indexOf("bookContent:"));
            bookContent = message.substring(message.indexOf("bookContent:") + 12).toString();
            List<String> info = new ArrayList<>();
            info.add(sessionID);
            info.add(bookID);
            info.add(bookContent);
            return info;
        }
        return null;
    }

    public List<String> parseNewBook(String message){
        String sessionID;
        String title;
        String text;
        if (message.startsWith("sessionID:") && message.contains("title:") && message.contains("text:")){
            sessionID = message.substring(9, message.indexOf("title:"));
            message = message.substring(message.indexOf("title:"));
            title = message.substring(5, message.indexOf("title:"));
            text = message.substring(message.indexOf("title:") + 10).toString();
            List<String> info = new ArrayList<>();
            info.add(sessionID);
            info.add(title);
            info.add(text);
            return info;
        }
        return null;
    }

    public String parseGetBookList(String message){
        String sessionID;
        if (message.startsWith("sessionID")){
            sessionID =  message.substring(9);
        }
        return null;
    }

}
