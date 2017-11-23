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
                message.setMessage(messageInside.substring(16));
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
        if (message.startsWith("sessionID:") && message.contains("bookID:")) {
            bookID = message.substring(10, message.indexOf("bookID:"));
            sessionID = message.substring(message.indexOf("bookID:") + 7).toString();
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
        String[] array = message.split("(sessionID:|bookID:|bookContent:)");
        if (array.length == 4){
            List<String> info = new ArrayList<>();
            info.add(array[1]);
            info.add(array[2]);
            info.add(array[3]);
            return info;
        }
        return null;
    }

    public List<String> parseNewBook(String message){
        String sessionID;
        String title;
        String text;
        String[] array = message.split("(sessionID:|bookTitle:|bookText:)");
        if (array.length == 4){
            List<String> info = new ArrayList<>();
            sessionID = array[1];
            info.add(sessionID);
            title = array[2];
            info.add(title);
            text = array[3];
            info.add(text);
            return info;
        }
        return null;
    }

    public String parseGetBookList(String message){
        String sessionID;
        String[] array = message.split("sessionID:");
        if (array.length == 2){
            sessionID =  array[1];
            return sessionID;
        }
        return null;
    }

}
