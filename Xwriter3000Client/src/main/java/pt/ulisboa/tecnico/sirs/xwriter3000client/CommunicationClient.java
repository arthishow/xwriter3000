package pt.ulisboa.tecnico.sirs.xwriter3000client;


import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationClient {

    private String sessionID;

    public CommunicationClient() {
    }


    //think this is better a boolean
    public Boolean sendBookChanges(String book, String sessionID) {
        String messageContent;
        messageContent = "type:" + "receiveBookChanges" + "book:" + book + "sessionID" + sessionID;
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add decipher
            //add some verification
            return true;
        } catch (IOException e) {
            System.out.println("ServerProblems");
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
            return false;
        }
    }

    public String getBook(String bookID, String sessionID) {
        String messageContent;
        messageContent = "type:" + "sendbook" + "bookID:" + bookID + "sessionID:" + sessionID;
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
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Server problems");
            return null;
        }
    }

    public void createUser(String username, String password){
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
    }

    public void createBook(String title, String text){
        String messageContent;
        messageContent = "type:" + "sessionID:" + sessionID + "createBook" + "title:" + title + "text:" + text;
        //add cypher
        Message message = new Message(messageContent, "");
        try {
            Message replay = sendMessageReplay(message);
            //add a decipher function
            //add some more stuff
            System.out.println(replay.getMessage());
        } catch (IOException e){
            System.out.println("Server has problems");
        } catch (ClassNotFoundException e) {
            System.out.println("Server  problems");
        }
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
