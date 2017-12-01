package pt.ulisboa.tecnico.sirs.xwriter3000client;


import com.sun.org.apache.xpath.internal.operations.Bool;
import pt.ulisboa.tecnico.sirs.xwriter3000.Message;
import pt.ulisboa.tecnico.sirs.xwriter3000ui.Book;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class CommunicationClient {

    private String sessionID;

    public CommunicationClient() {
    }

    public Boolean createUser(String username, String password){
        String messageContent;
        messageContent = "type:" + "createUser" + "username:" + username + "password:" + password;
        //ciphermessage
        Message message = new Message(messageContent, "");

        Message replay = sendMessageReplay(message);
            //add decipher
            //add some more important stuff
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
                //add decipher
        }
        return false;
    }


    public Boolean authenticateUser(String username, String password) {
        String messageContent;
        messageContent = "type:" + "authenticateUser" + "username:" + username + "password:" + password;
        //ciphermessage
        Message message = new Message(messageContent, "");
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
        sessionID = replay.getMessage();
            //add check
        if (sessionID != null) {
            return true;
            //add decipher
        }
        return false;
    }

    public int createBook(String title){
        String messageContent;
        messageContent = "type:" + "createBook:" + "sessionID:" + sessionID  + "bookTitle:" + title;
        //add cypher
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        //add a decipher function
        //add some more stuff
        return Integer.valueOf(replay.getMessage());
    }


    public int createBook(String title, Map<String, Integer> authorsAuth){
        int bookID = createBook(title);
        if (!authorsAuth.isEmpty()) {
            addAuthorsAuth(String.valueOf(bookID), authorsAuth);
        }
        return bookID;
    }

    public String getBook(String bookID) {
        String messageContent;
        messageContent = "type:" + "getBook" + "sessionID:" + sessionID + "bookID:" + bookID;
        //ciphermessage
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
        return replay.getMessage();
    }



    //think this is better a boolean
    public Boolean sendBookChanges(String bookID, String bookContent) {
        String messageContent;
        messageContent = "type:" + "receiveBookChanges" + "sessionID:" + sessionID + "bookID:" + bookID + "bookContent:" + bookContent;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
                //add decipher
        }
        return false;
    }


    public List<Book> getBookList(){
        String messageContent;
        messageContent = "type:getBookListsessionID:" + sessionID;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        System.out.println(replay.getMessage());
        //add decipher
        String[] bookListString = replay.getMessage().split("book(ID:|Title:)");
        ArrayList<Book> bookList = new ArrayList<>();
        for (int i = 1; i < bookListString.length; i += 2){
            Book book = new Book(Integer.parseInt(bookListString[i]), bookListString[i + 1]);
            bookList.add(book);
        }
        return bookList;
    }


    public Boolean forwardSymKey() {
        return true;
    }

    public Boolean addAuthorsAuth(String bookID, Map<String, Integer> authorsAuth){
        String messageContent;
        messageContent = "type:addAuthorsAuthsessionID:" + sessionID + "bookID:" + bookID;
        for (Map.Entry<String, Integer> authorAuth : authorsAuth.entrySet()){
            messageContent += "username:" + authorAuth.getKey();
            messageContent += "auth:" + authorAuth.getValue();
        }
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
        }
        return false;
    }

    public Boolean authorExists(String username){
        String messageContent;
        messageContent = "type:authorExistsusername:" + username;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
        }
        return false;
    }

    public List<String> getAuthorsFromBook(String bookID){
        String messageContent;
        messageContent = "type:"+ "getAuthorsFromBook" +"sessionID:" + sessionID + "bookID:" + bookID;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        //add decipher
        String[] authors = replay.getMessage().split("username:");
        List<String> authorsList = new ArrayList<>();
        for(int i = 1; i < authors.length; i++) {
            authorsList.add(authors[i]);
        }
        return authorsList;
    }

    public void sendMessage(Message message) throws IOException {
        try {
            Socket clientSocket = new Socket("localhost", 8001);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            objectOut.close();
            clientSocket.close();
        } catch (IOException e){
            System.out.println("ServerProblems");
        }
    }

    public Message sendMessageReplay(Message message) {
        try{
            Socket clientSocket = new Socket("localhost", 8001);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
        return replay;
        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return null;
    }



}
