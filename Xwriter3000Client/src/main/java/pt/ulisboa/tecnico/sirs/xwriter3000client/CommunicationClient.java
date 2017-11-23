package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommunicationClient {

    private String sessionID;

    public CommunicationClient() {
    }

    public Boolean createUser(String username, String password){
        String messageContent;
        messageContent = "type:" + "createUser" + "username:" + username + "password:" + password;
        //ciphermessage
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some more important stuff
            System.out.println(replay.getMessage());
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server got problems");
        }
        return false;
    }

    public String authenticateUser(String username, String password) {
        String messageContent;
        messageContent = "type:" + "authenticateUser" + "username:" + username + "password:" + password;
        //ciphermessage
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
            sessionID = replay.getMessage();
            return replay.getMessage();
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return null;
    }

    public Boolean createBook(String title, String text){
        String messageContent;
        messageContent = "type:" + "createBook:" + "sessionID:" + sessionID  + "bookTitle:" + title + "bookText:" + text;
        //add cypher
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add a decipher function
            //add some more stuff
            System.out.println(replay.getMessage());
            return Boolean.valueOf(replay.getMessage());
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server  problems");
        }
        return false;
    }

    public String getBook(String bookID, String sessionID) {
        String messageContent;
        messageContent = "type:" + "sendbook" + "sessionID:" + sessionID + "bookID:" + bookID;
        //ciphermessage
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
            return replay.getMessage();
        } catch (IOException e) {
            System.out.println("Server problems");
            return null;
        }  catch (ClassNotFoundException e) {
            System.out.println("Server problems");
            return null;
        }
    }



    //think this is better a boolean
    public Boolean sendBookChanges(String bookID, String bookContent) {
        String messageContent;
        messageContent = "type:" + "receiveBookChanges" + "sessionID:" + sessionID + "bookID:" + bookID + "bookContent:" + bookContent;
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
            System.out.println(replay.getMessage());
            return true;
        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return false;
    }


    public List<ArrayList<String>> getBookList(){
        String messageContent;
        messageContent = "type:getBookListsessionID:" + sessionID;
        Message message = new Message(messageContent, "");
        try{
            Message replay = sendMessageReplay(message);
            //add decipher
            String[] bookListString = replay.getMessage().split("book(ID:|Title:)");
            List<String> badBookList = Arrays.asList(bookListString);
            ArrayList<ArrayList<String>> bookList = new ArrayList<ArrayList<String>>();

            for (Iterator<String> bookIterator = badBookList.iterator(); bookIterator.hasNext();){
                ArrayList<String> book = new ArrayList<>();
                bookIterator.next();
                String bookID = bookIterator.next();
                book.add(bookID);
                String bookTitle = bookIterator.next();
                book.add(bookTitle);
                bookList.add(book);
            }
            return bookList;

        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return null;
    }


    public boolean forwardSymKey() {
        return true;
    }

    public void sendMessage(Message message) throws IOException {
        Socket clientSocket = new Socket("localhost", 8001);
        ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
        objectOut.writeObject(message);
        objectOut.close();
        clientSocket.close();
    }

    public Message sendMessageReplay(Message message) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("localhost", 8001);
        ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
        objectOut.writeObject(message);
        ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
        Message replay = (Message) objectIn.readObject();
        return replay;
    }

}
