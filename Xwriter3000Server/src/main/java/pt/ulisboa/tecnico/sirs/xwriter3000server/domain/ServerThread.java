package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class ServerThread extends Thread {
    private Socket clientSocket;

    private CommunicationServer communicationServer;

    private CypherUtil cypherUtil;

    private MessageParser parser;

    private Message message;

    private ActiveUser currentUser;

    public ServerThread(Socket clientSocket, CommunicationServer communicationServer, CypherUtil cypherUtil){
        this.clientSocket = clientSocket;
        this.communicationServer = communicationServer;
        this.cypherUtil = cypherUtil;
        parser = new MessageParser();


    }

    public void run(){

        try {
            ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            message = (Message) inFromClient.readObject();
            String originalMessage = message.getMessage();
            message.setMessage(cypherUtil.decypherMessage(message.getMessage()));
            message = parser.parseType(message);
            ActiveUser activeUser;

            switch (message.getType()) {
                case "createUser":
                    Message secret = (Message) inFromClient.readObject();
                    Message publicKey = (Message) inFromClient.readObject();
                    createUser(message, secret, publicKey);
                    break;
                case "authenticateUser":
                    authenticateUser(message);
                    break;
                case "createBook":
                    List<String> newBook = parser.parseNewBook(message.getMessage());
                    activeUser = communicationServer.activeUser(newBook.get(0));
                    if(activeUser != null){
                        if(cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())){
                            createBook(activeUser, newBook);
                        }
                    }
                    break;
                case "getBook":
                    List<String> bookInfo = parser.parseGetBook(message.getMessage());
                    activeUser = communicationServer.activeUser(bookInfo.get(0));
                    if(activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getBook(activeUser, bookInfo);
                        }
                    }
                    break;
                case "receiveBookChanges":
                    List<String> bookChanges = parser.parseReceiveBookChanges(message.getMessage());
                    activeUser = communicationServer.activeUser(bookChanges.get(0));
                    if(activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            receiveBookChanges(activeUser, bookChanges);
                        }
                    }
                    break;
                case "getBookList":
                    String sessionID = parser.parseGetBookList(message.getMessage());
                    activeUser = communicationServer.activeUser(sessionID);
                    if(activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getBookList(activeUser);
                        }
                    }
                    break;
                case "forwardSymKey":
                    //fixme
                    //server.forwardSymKey();
                    break;
                case "addAuthorsAuth":
                    List<String> bookAuthorsAuth = parser.parseAddAuthorAuth(message.getMessage());
                    activeUser = communicationServer.activeUser(bookAuthorsAuth.get(0));
                    if (activeUser != null){
                        if(cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())){
                            addAuthorAuth(activeUser, bookAuthorsAuth);
                        }
                    }
                    break;
                case "authorExists":
                    authorExists(message);
                    break;
                case "getAuthorsFromBook":
                    List<String> userAndBook = parser.getAuthorsFromBook(message.getMessage());
                    activeUser = communicationServer.activeUser(userAndBook.get(0));
                    if (activeUser != null){
                        if(cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())){
                            getAuthorsFromBook(activeUser, userAndBook);
                        }
                    }
                    break;
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    public void createUser(Message message, Message secret, Message publicKey){
        List<String> userInfo = parser.parseCreateUser(message.getMessage());
        if (userInfo != null) {
            Boolean success = false;
            byte[] macKeyBytes = Base64.getDecoder().decode(userInfo.get(2));

            SecretKey macKey = new SecretKeySpec(macKeyBytes, 0, macKeyBytes.length, "HmacSHA512");


            if(cypherUtil.checkHmac(secret.getMessage(), secret.getSignature(), macKey) &&
                    cypherUtil.checkHmac(publicKey.getMessage(), publicKey.getSignature(), macKey)){
                success = communicationServer.createUser(userInfo.get(0), userInfo.get(1), secret.getMessage(), publicKey.getMessage());
            }

            Message replay = new Message(success.toString(), "");
            sendMessage(replay);
        }
    }

    public void authenticateUser(Message message){
        List<String> credentials = parser.parseUserInfo(message.getMessage());
        if (credentials != null) {
            ActiveUser activeUser = communicationServer.authenticateUser(credentials.get(0), credentials.get(1));
            //add cypher
            String replay = activeUser.getSessionID();
            sendSecureMessage(replay, activeUser);
        }
    }

    public void createBook(ActiveUser activeUser, List<String> newBook){
        if (newBook != null){
            int bookID = communicationServer.createBook(activeUser, newBook.get(1));
            sendSecureMessage(String.valueOf(bookID), activeUser);
        }
    }

    public void addAuthorAuth(ActiveUser activeUser, List<String> bookAuthorsAuth){
        if (bookAuthorsAuth != null && activeUser != null){
            String bookID = bookAuthorsAuth.get(1);
            Map<String, Integer> authorAuth = new HashMap<>();
            for (int i = 2; i < bookAuthorsAuth.size(); i += 2){
                authorAuth.put(bookAuthorsAuth.get(i), Integer.valueOf(bookAuthorsAuth.get(i + 1)));
            }
            Boolean success = communicationServer.addAuthorAuth(activeUser, bookID, authorAuth);
            sendSecureMessage(success.toString(), activeUser);
        }

    }

    public void getBook(ActiveUser activeUser, List<String> bookInfo){
        if (bookInfo != null) {
            String book = communicationServer.sendBook(activeUser, bookInfo.get(1));
            sendSecureMessage(book, activeUser);
        }
    }

    public void receiveBookChanges(ActiveUser activeUser, List<String> bookChanges){
        if (bookChanges != null){
            Boolean success = communicationServer.receiveBookChanges(activeUser, bookChanges.get(1), bookChanges.get(2));
            sendSecureMessage(success.toString(), activeUser);
        }
    }

    public void getBookList(ActiveUser activeUser){
        if (activeUser != null){
            List<Book> bookList = new ArrayList<>();
            bookList.addAll(communicationServer.getBookList(activeUser));
            String replayMessage = "";

            for (Book book: bookList){
                replayMessage += "bookID:" + book.getBookID() + "bookTitle:" + book.getTitle();
            }
            sendSecureMessage(replayMessage, activeUser);
        }
    }

    public void authorExists(Message message){
        String username = parser.authorExists(message.getMessage());

        if (username != null){
            Boolean success = communicationServer.authorExists(username);
            Message replay = new Message(success.toString(), "");
            sendMessage(replay);
        }

    }

    public void getAuthorsFromBook(ActiveUser activeUser, List<String> userAndBook){
        if (userAndBook != null) {
            List<String> authors = new ArrayList<>();
            authors.addAll(communicationServer.getAuthorsFromBook(activeUser, userAndBook.get(1)));

            String replayMessage = "";

            for (String author: authors){
                replayMessage += "username:" + author;
            }
            sendSecureMessage(replayMessage, activeUser);
        }
    }


    public void sendMessage(Message message){
        try {
            //cipher and sign message
            ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSecureMessage(String replay, ActiveUser activeUser){
        try {
            String signature = cypherUtil.getSiganture(replay);
            replay = cypherUtil.cypherMessage(replay, activeUser.getPublicKey());
            Message message = new Message(replay, signature);
            ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            outToClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
