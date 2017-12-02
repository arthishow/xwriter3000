package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;


import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageParser {

    public Message parseType(Message message){
        String messageInside = message.getMessage();
        if (messageInside.startsWith("type:")){
            if(messageInside.indexOf("getBookList") == 5){
                message.setType("getBookList");
                message.setMessage(messageInside.split("getBookList")[1]);
                return message;
            }
            else if(messageInside.indexOf("getBook") == 5){
                message.setType("getBook");
                message.setMessage(messageInside.split("getBook")[1]);
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
            else if(messageInside.indexOf("createBook") == 5){
                message.setType("createBook");
                message.setMessage(messageInside.substring(16));
                return message;
            }
            else if (messageInside.indexOf("logout") == 5){
                message.setType("logout");
                message.setMessage(messageInside.substring(11));
                return message;
            }
            else if(messageInside.indexOf("addAuthorsAuth") == 5){
                message.setType("addAuthorsAuth");
                message.setMessage(messageInside.split("addAuthorsAuth")[1]);
                return message;
            }
            else if(messageInside.indexOf("authorExists") == 5){
                message.setType("authorExists");
                message.setMessage(messageInside.split("authorExists")[1]);
                return message;
            }
            else if(messageInside.indexOf("getAuthorsFromBook") == 5){
                message.setType("getAuthorsFromBook");
                message.setMessage(messageInside.split("getAuthorsFromBook")[1]);
                return message;
            }
            else if(messageInside.indexOf("alarm") == 5) {
                message.setType("alarm");
                message.setMessage(messageInside.split("alarm")[1]);
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
    public List<String> parseGetBook(String message){
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
        String[] array = message.split("(sessionID:|bookTitle:)");
        if (array.length == 3){
            List<String> info = new ArrayList<>();
            sessionID = array[1];
            info.add(sessionID);
            title = array[2];
            info.add(title);
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

    public List<String> parseAddAuthorAuth(String message){
        List<String> ids = new ArrayList<>();
        String[] array = message.split("sessionID:|bookID:|username:|auth:");
        if (array.length > 3){
            for (int i = 1; i < array.length; i++){
                ids.add(array[i]);
            }
            return ids;
        }
        return null;
    }

    public String authorExists(String message) {
        String[] array = message.split("username:");
        if (array.length == 2){
            return array[1];
        }
        return null;
    }

    public List<String> getAuthorsFromBook(String message){
        String[] array = message.split("sessionID:|bookID:");
        if (array.length == 3){
            List<String> info = new ArrayList<>();
            info.add(array[1]);
            info.add(array[2]);
            return info;
        }
        return null;
    }

    public Date parseAlarm(String message){
            try {
                DateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                Date date = format.parse(message);
                return date;
            }catch(Exception e) {
                System.out.print("Error in parseAlarm");
                return null;
            }
        }
    }


