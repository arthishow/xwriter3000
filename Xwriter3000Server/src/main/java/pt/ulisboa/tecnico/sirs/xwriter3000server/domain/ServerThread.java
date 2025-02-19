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


    private ObjectInputStream inFromClient;

    private ObjectOutputStream outToClient;


    public ServerThread(Socket clientSocket, CommunicationServer communicationServer, CypherUtil cypherUtil) {
        this.clientSocket = clientSocket;
        this.communicationServer = communicationServer;
        this.cypherUtil = cypherUtil;
        parser = new MessageParser();


    }

    public void run() {

        try {
            inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            message = (Message) inFromClient.readObject();
            String originalMessage = message.getMessage();
            message.setMessage(cypherUtil.decypherMessage(message.getMessage()));
            message = parser.parseType(message);
            ActiveUser activeUser;
            Message secret;
            switch (message.getType()) {
                case "createUser":
                    secret = (Message) inFromClient.readObject();
                    Message publicKey = (Message) inFromClient.readObject();
                    createUser(message, secret, publicKey);
                    break;
                case "authenticateUser":
                    authenticateUser(message);
                    break;
                case "createBook":
                    secret = (Message) inFromClient.readObject();
                    List<String> newBook = parser.parseNewBook(message.getMessage());
                    activeUser = communicationServer.activeUser(newBook.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(secret.getMessage(), secret.getSignature(), activeUser.getPublicKey()) &&
                                cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            String secretKey = cypherUtil.decypherMessage(secret.getMessage());
                            createBook(activeUser, newBook, secretKey);
                        }
                    }
                    break;
                case "getBook":
                    List<String> bookInfo = parser.parseGetBook(message.getMessage());
                    activeUser = communicationServer.activeUser(bookInfo.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getBook(activeUser, bookInfo);
                        }
                    }
                    break;
                case "receiveBookChanges":
                    List<String> bookChanges = parser.parseReceiveBookChanges(message.getMessage());
                    activeUser = communicationServer.activeUser(bookChanges.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            receiveBookChanges(activeUser, bookChanges);
                        }
                    }
                    break;
                case "getBookList":
                    String sessionID = parser.parseGetBookList(message.getMessage());
                    activeUser = communicationServer.activeUser(sessionID);
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getBookList(activeUser);
                        }
                    }
                    break;
                case "addAuthorsAuth":
                    List<String> bookAuthorsAuth = parser.parseAddAuthorAuth(message.getMessage());
                    activeUser = communicationServer.activeUser(bookAuthorsAuth.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
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
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getAuthorsFromBook(activeUser, userAndBook);
                        }
                    }
                    break;
                case "removeAuthor":
                    List<String> bookRemove = parser.parseRemoveAuthor(message.getMessage());
                    activeUser = communicationServer.activeUser(bookRemove.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            remAuthor(activeUser, bookRemove.get(1), bookRemove.get(2));
                        }
                    }
                    break;
                case "logout":
                    sessionID = parser.getSessionID(message.getMessage());
                    activeUser = communicationServer.activeUser(sessionID);
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            Boolean logout = communicationServer.logout(sessionID, activeUser.getUsername());

                            if (logout) {
                                sendSecureMessage(logout.toString(), activeUser);
                            }
                        }
                    }
                    break;
                case "getAuthFromBook":
                    List<String> bookAuth = parser.parseRemoveAuthor(message.getMessage());
                    activeUser = communicationServer.activeUser(bookAuth.get(0));
                    if (activeUser != null) {
                        if (cypherUtil.verifySignature(originalMessage, message.getSignature(), activeUser.getPublicKey())) {
                            getAuthFromBook(activeUser, bookAuth.get(1), bookAuth.get(2));
                        }
                    }
                    break;
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getAuthFromBook(ActiveUser activeUser, String bookID, String username) {
        Integer auth = communicationServer.getAuthFromBook(bookID, username);
        sendSecureMessage(auth.toString(), activeUser);
    }

    public Boolean remAuthor(ActiveUser activeUser, String bookID, String exiledAuthor) throws IOException, ClassNotFoundException {
        Boolean success = communicationServer.removeUser(activeUser.getUsername(), bookID, exiledAuthor);
        if (success) {
            if (communicationServer.checkSymKey(bookID, exiledAuthor)) {
                communicationServer.removeSymKey(bookID, exiledAuthor);
            } else if (communicationServer.checkTempKey(bookID, exiledAuthor)) {
                communicationServer.removeTempKey(bookID, exiledAuthor);
            }

            List<String> authors = communicationServer.getAuthorsFromBook(activeUser, bookID);
            for (String author : authors) {
                sendPublicKeyMessage(communicationServer.getPublicKey(author), activeUser);

                Message newKey = (Message) inFromClient.readObject();

                if (cypherUtil.verifySignature(newKey.getMessage(), newKey.getSignature(), activeUser.getPublicKey())) {

                    if (communicationServer.checkSymKey(bookID, author)) {
                        communicationServer.removeSymKey(bookID, author);
                    } else if (communicationServer.checkTempKey(bookID, author)) {
                        communicationServer.removeTempKey(bookID, author);
                    }
                    communicationServer.storeTempKey(author, bookID, newKey.getMessage());
                }
            }
            Message book = (Message) inFromClient.readObject();
            if (cypherUtil.verifySignature(book.getMessage(), book.getSignature(), activeUser.getPublicKey())) {
                communicationServer.receiveBookChanges(activeUser, bookID, book.getMessage());
            }
            Message authorKey = (Message) inFromClient.readObject();
            if (cypherUtil.verifySignature(authorKey.getMessage(), authorKey.getSignature(), activeUser.getPublicKey())) {
                communicationServer.updateSecretKey(activeUser.getUsername(), bookID, authorKey.getMessage());
            }

            sendSecureMessage(success.toString(), activeUser);
            return true;
        }
        sendSecureMessage(success.toString(), activeUser);
        return false;
    }


    public void createUser(Message message, Message secret, Message publicKey) {
        List<String> userInfo = parser.parseCreateUser(message.getMessage());
        if (userInfo != null) {
            Boolean success = false;
            byte[] macKeyBytes = Base64.getDecoder().decode(userInfo.get(2));

            SecretKey macKey = new SecretKeySpec(macKeyBytes, 0, macKeyBytes.length, "HmacSHA512");


            if (cypherUtil.checkHmac(secret.getMessage(), secret.getSignature(), macKey) &&
                    cypherUtil.checkHmac(publicKey.getMessage(), publicKey.getSignature(), macKey)) {
                success = communicationServer.createUser(userInfo.get(0), userInfo.get(1), secret.getMessage(), publicKey.getMessage());
            }

            Message replay = new Message(success.toString(), cypherUtil.getSiganture(success.toString()));
            sendMessage(replay);
        }
    }

    public void authenticateUser(Message message) {
        List<String> credentials = parser.parseUserInfo(message.getMessage());
        if (credentials != null) {
            ActiveUser activeUser = communicationServer.authenticateUser(credentials.get(0), credentials.get(1));
            if (activeUser != null) {
                if (Boolean.valueOf(credentials.get(2))) {
                    Message secret = new Message(communicationServer.getPrivateKey(activeUser.getUsername()), "");
                    secret.setSignature(cypherUtil.getSiganture(secret.getMessage()));
                    sendMessage(secret);
                    String replay = activeUser.getSessionID();
                    sendSecureMessage(replay, activeUser);
                } else {
                    String replay = activeUser.getSessionID();
                    sendSecureMessage(replay, activeUser);
                }
            }
        }
    }

    public void createBook(ActiveUser activeUser, List<String> newBook, String secretKey) {
        if (newBook != null) {
            int bookID = communicationServer.createBook(activeUser, newBook.get(1), secretKey);
            sendSecureMessage(String.valueOf(bookID), activeUser);
        }
    }

    public void addAuthorAuth(ActiveUser activeUser, List<String> bookAuthorsAuth) {
        if (bookAuthorsAuth != null && activeUser != null) {
            String bookID = bookAuthorsAuth.get(1);
            Map<String, Integer> authorAuth = new HashMap<>();

            String secretKey = communicationServer.getSecretKey(activeUser.getUsername(), bookID);

            for (int i = 2; i < bookAuthorsAuth.size(); i += 2) {
                authorAuth.put(bookAuthorsAuth.get(i), Integer.valueOf(bookAuthorsAuth.get(i + 1)));
            }

            String tempKey;
            Boolean success = communicationServer.addAuthorAuth(activeUser, bookID, authorAuth);
            sendSecureMessage(secretKey, activeUser);
            for (String username : authorAuth.keySet()) {
                String publicKey = communicationServer.getPublicKey(username);
                sendPublicKeyMessage(publicKey, activeUser);
                tempKey = receiveMessage(activeUser);
                if (!communicationServer.checkTempKey(bookID, username)) {
                    communicationServer.storeTempKey(username, bookID, tempKey);
                }
            }

            sendSecureMessage(success.toString(), activeUser);
        }
    }

    public String receiveMessage(ActiveUser activeUser) {
        try {
            Message message = (Message) inFromClient.readObject();
            if (message != null) {
                if (cypherUtil.verifySignature(message.getMessage(), message.getSignature(), activeUser.getPublicKey())) {
                    return message.getMessage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getBook(ActiveUser activeUser, List<String> bookInfo) {
        if (bookInfo != null) {
            String book = communicationServer.sendBook(activeUser, bookInfo.get(1));
            if (book.contains("tempKey")) {
                try {
                    Message message = new Message(book, cypherUtil.getSiganture(book));
                    sendMessage(message);
                    Message newKey = (Message) inFromClient.readObject();
                    if (cypherUtil.verifySignature(newKey.getMessage(), newKey.getSignature(), activeUser.getPublicKey())) {
                        String symKey = cypherUtil.decypherMessage(newKey.getMessage());
                        communicationServer.setSecretKey(activeUser.getUsername(), bookInfo.get(1), symKey);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Message message = new Message(book, cypherUtil.getSiganture(book));
                sendMessage(message);
            }
        }
    }

    public void receiveBookChanges(ActiveUser activeUser, List<String> bookChanges) {
        try {
            if (bookChanges != null) {
                Message book = (Message) inFromClient.readObject();
                if (book != null || cypherUtil.verifySignature(book.getMessage(), book.getSignature(), activeUser.getPublicKey())) {
                    Boolean success = communicationServer.receiveBookChanges(activeUser, bookChanges.get(1), book.getMessage());
                    sendSecureMessage(success.toString(), activeUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getBookList(ActiveUser activeUser) {
        if (activeUser != null) {
            List<Book> bookList = new ArrayList<>();
            bookList.addAll(communicationServer.getBookList(activeUser));
            String replayMessage = "";

            for (Book book : bookList) {
                replayMessage += "bookID:" + book.getBookID() + "bookTitle:" + book.getTitle();
            }
            sendSecureMessage(replayMessage, activeUser);
        }
    }

    public void authorExists(Message message) {
        String username = parser.authorExists(message.getMessage());

        if (username != null) {
            Boolean success = communicationServer.authorExists(username);
            Message replay = new Message(success.toString(), cypherUtil.getSiganture(success.toString()));
            sendMessage(replay);
        }

    }

    public void getAuthorsFromBook(ActiveUser activeUser, List<String> userAndBook) {
        if (userAndBook != null) {
            List<String> authors = new ArrayList<>();
            authors.addAll(communicationServer.getAuthorsFromBook(activeUser, userAndBook.get(1)));

            String replayMessage = "";

            for (String author : authors) {
                replayMessage += "username:" + author;
            }
            sendSecureMessage(replayMessage, activeUser);
        }
    }


    public void sendMessage(Message message) {
        try {
            outToClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSecureMessage(String replay, ActiveUser activeUser) {
        try {
            replay = cypherUtil.cypherMessage(replay, activeUser.getPublicKey());
            String signature = cypherUtil.getSiganture(replay);
            Message message = new Message(replay, signature);
            outToClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicKeyMessage(String publicKey, ActiveUser activeUser) {
        try {
            String signature = cypherUtil.getSiganture(publicKey);
            Message message = new Message(publicKey, signature);
            outToClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
