package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.util.ArrayList;
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
            else if(messageInside.indexOf("removeAuthor") == 5){
                message.setType("removeAuthor");
                message.setMessage(messageInside.split("removeAuthor")[1]);
                return message;
            }
        }
        return null;
    }

    public List<String> parseCreateUser(String message){
        String[] array = message.split("(username:|password:|MAC:)");
        if(array.length == 4){
            List<String> infoUser = new ArrayList<>();
            infoUser.add(array[1]);
            infoUser.add(array[2]);
            infoUser.add(array[3]);
            return infoUser;
        }
        return null;
    }

    public List<String> parseUserInfo(String message){
        System.out.println(message);
        String[] array = message.split("(username:|password:|newMachine:)");
        if (array.length == 4) {
            List<String> auth = new ArrayList<>();
            auth.add(array[1]);
            auth.add(array[2]);
            auth.add(array[3]);
            return auth;
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
        String[] array = message.split("(sessionID:|bookID:)");
        if (array.length == 3){
            List<String> info = new ArrayList<>();
            info.add(array[1]);
            info.add(array[2]);
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

    public List<String> parseRemoveAuthor(String message){
        List<String> remAuth = new ArrayList<>();
        System.out.println(message);
        String[] array = message.split("sessionID:|bookID:|username:");
        System.out.println(array.length);
        if(array.length == 4){
            System.out.println(array[1]);
            System.out.println(array[2]);
            System.out.println(array[3]);
            remAuth.add(array[1]);
            remAuth.add(array[2]);
            remAuth.add(array[3]);
            return remAuth;
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

}
