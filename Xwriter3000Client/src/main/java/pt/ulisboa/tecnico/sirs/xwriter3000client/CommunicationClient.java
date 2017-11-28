package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000.Message;
import pt.ulisboa.tecnico.sirs.xwriter3000ui.Book;

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
            return Boolean.valueOf(replay.getMessage());
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server got problems");
        }
        return false;
    }


    public Boolean authenticateUser(String username, String password) {
        String messageContent;
        messageContent = "type:" + "authenticateUser" + "username:" + username + "password:" + password;
        //ciphermessage
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
            sessionID = replay.getMessage();
            //add check
            return true;
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return false;
    }

    public int createBook(String title){
        String messageContent;
        messageContent = "type:" + "createBook:" + "sessionID:" + sessionID  + "bookTitle:" + title;
        //add cypher
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add a decipher function
            //add some more stuff
            return Integer.valueOf(replay.getMessage());
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server  problems");
        }
        return -1;
    }


    public int createBook(String title, List<String> userID){
        int bookID = createBook(title);
        if (!userID.isEmpty()) {
            addAuthorsAuth(String.valueOf(bookID), userID);
        }
        return bookID;
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


    public List<Book> getBookList(){
        String messageContent;
        messageContent = "type:getBookListsessionID:" + sessionID;
        Message message = new Message(messageContent, "");
        try{
            Message replay = sendMessageReplay(message);
            //add decipher
            String[] bookListString = replay.getMessage().split("book(ID:|Title:)");
            ArrayList<Book> bookList = new ArrayList<Book>();
            System.out.println(replay.getMessage());
            for (int i = 1; i < bookListString.length; i += 2){
                System.out.println(bookListString[i]);
                System.out.println(bookListString[i + 1]);
                Book book = new Book(Integer.parseInt(bookListString[i]), bookListString[i + 1]);
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

    public boolean addAuthorsAuth(String bookID, List<String> authorIDs){
        String messageContent;
        messageContent = "type:addAuthorAuthsessioID:" + sessionID + "bookID:" + bookID;
        for (String authorID : authorIDs){
            messageContent += "authorID:" + authorID;
        }
        Message message = new Message(messageContent, "");
        try{
            Message replay = sendMessageReplay(message);
            //add deciphera
            return true;
        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return false;
    }

    public boolean authorExists(String username){
        String messageContent;
        messageContent = "type:addAuthorAuthsessioID:" + sessionID + "username:" + username;
        Message message = new Message(messageContent, "");
        try{
            Message replay = sendMessageReplay(message);
            return true;
            //add decipher
        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return false;
    }

    public List<String> getAuthorsFromBook(String bookID){
        String messageContent;
        messageContent = "type:getAuthorsFromBooksessioID:" + sessionID + "bookID:" + bookID;
        Message message = new Message(messageContent, "");
        try{
            Message replay = sendMessageReplay(message);
            //add decipher
            String[] authors = replay.getMessage().split("username:");
            List<String> authorsList= new ArrayList<>();
            for(int i = 1; i < authors.length; i++) {
                authorsList.add(authors[i]);
            }
        } catch (IOException e) {
            System.out.println("ServerProblems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
        }
        return null;
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
