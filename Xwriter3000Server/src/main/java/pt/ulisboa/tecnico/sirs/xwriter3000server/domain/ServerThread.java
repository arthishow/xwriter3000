package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerThread extends Thread {
    private Socket clientSocket;

    private CommunicationServer communicationServer;

    private MessageParser parser;

    private Message message;

    public ServerThread(Socket clientSocket, CommunicationServer communicationServer){
        this.clientSocket = clientSocket;
        this.communicationServer = communicationServer;
        parser = new MessageParser();
    }

    public void run(){

        try {
            ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            message = (Message) inFromClient.readObject();
            message = parser.parseType(message);
            switch (message.getType()) {
                case "createUser":
                    createUser(message);
                    break;
                case "authenticateUser":
                    authenticateUser(message);
                    break;
                case "createBook":
                    createBook(message);
                    break;
                case "sendBook":
                    sendBook(message);
                    break;
                case "receiveBookChanges":
                    receiveBookChanges(message);
                    break;
                case "getBookList":
                    getBookList(message);
                    break;
                case "forwardSymKey":
                    //fixme
                    //server.forwardSymKey();
                    break;
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Problem");
        } catch (ClassNotFoundException e){
            System.out.println("Problem with the object class");
        }
    }


    public void createUser(Message message){
        List<String> userInfo = parser.parseUserInfo(message.getMessage());
        if (userInfo != null) {
            Boolean success = communicationServer.createUser(userInfo.get(0), userInfo.get(1));
            //add cypher
            Message replay = new Message(success.toString(), "");
            sendMessage(replay);
        }
    }

    public void authenticateUser(Message message){
        List<String> credentials = parser.parseUserInfo(message.getMessage());
        if (credentials != null) {
            String sessionID = communicationServer.authenticateUser(credentials.get(0), credentials.get(1));
            //add cypher
            Message replay = new Message(sessionID, "");
            sendMessage(replay);
        }
    }

    public void createBook(Message message){
        List<String> book = parser.parseNewBook(message.getMessage());
        if (book != null){
            Boolean success = communicationServer.createBook(book.get(0), book.get(1), book.get(2));
            //add cypher
            Message replay = new Message(success.toString(), "");
            sendMessage(replay);
        }
    }

    public void sendBook(Message message){
        List<String> bookInfo = parser.parseSendBook(message.getMessage());
        if (bookInfo != null) {
            String book = communicationServer.sendBook(bookInfo.get(0), bookInfo.get(1));
            //add cypher
            Message replay = new Message(book, "");
            sendMessage(replay);
        }
    }

    public void receiveBookChanges(Message message){
        List<String> info = parser.parseReceiveBookChanges(message.getMessage());
        if (info != null){
            Boolean success = communicationServer.receiveBookChanges(info.get(0), info.get(1), info.get(2));
            //add cypher
            Message replay = new Message(success.toString(), "");
            sendMessage(replay);
        }
    }

    public void getBookList(Message message){
        String sessionID = parser.parseGetBookList(message.getMessage());
        if (sessionID != null){
            List<ArrayList<String>> bookList = communicationServer.getBookList(sessionID);
            String replayMessage = "";

            for (Iterator<ArrayList<String>> bookIterator = bookList.iterator(); bookIterator.hasNext();){
                ArrayList<String> book = bookIterator.next();
                replayMessage += "bookID:" + book.get(0) + "bookTitle:" + book.get(1);
            }

            //add cypher
            Message replay = new Message(replayMessage, "");
            sendMessage(replay);
        }
    }


    public void sendMessage(Message message){
        try {
            ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(message);
        } catch (IOException e) {
            System.out.println("Problem");
        }
    }

}
