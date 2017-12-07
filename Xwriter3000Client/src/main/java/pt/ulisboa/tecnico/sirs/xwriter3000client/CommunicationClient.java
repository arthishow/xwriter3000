package pt.ulisboa.tecnico.sirs.xwriter3000client;


import com.sun.org.apache.xpath.internal.operations.Bool;
import pt.ulisboa.tecnico.sirs.xwriter3000.Message;
import pt.ulisboa.tecnico.sirs.xwriter3000.Book;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommunicationClient {

    private String sessionID;

    private CypherUtil cypherUtil;

    private String salt;

    private String username;

    private String password;

    private SecretKey currentBookKey;

    private ObjectOutputStream objectOut;

    private ObjectInputStream objectIn;


    public CommunicationClient() {
        cypherUtil = new CypherUtil();
        cypherUtil.readServerPublicKey();
    }

    public Boolean createUser(String username, String password) {
        List<String> keyPair = cypherUtil.generateKeyPair(username);

        salt = cypherUtil.generateSalt(username);

        SecretKey macKey = cypherUtil.generateMac();
        String encodedMacKey = Base64.getEncoder().encodeToString(macKey.getEncoded());


        String messageContent = "type:" + "createUser" + "username:" + username + "password:" + password + "MAC:" + encodedMacKey;

        messageContent = cypherUtil.cypherMessage(messageContent);


        String secretString = cypherUtil.cipherPrivate(password, salt, keyPair.get(1));

        String publicKeyString = keyPair.get(0);


        Message replay = createUserMessage(messageContent, secretString, publicKeyString, macKey);
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
            //add decipher
        }
        return false;
    }

    //fixme
    public Boolean authenticateUser(String username, String password, Boolean newMachine) {
        this.username = username;
        this.password = password;
        String messageContent;
        salt = cypherUtil.readSalt(username);

        if (!newMachine) {
            cypherUtil.readFromFile(username);
        }

        messageContent = "type:" + "authenticateUser" + "username:" + username + "password:" + password
                + "newMachine:" + newMachine.toString();
        Message message = new Message(messageContent, "");
        Message replay = sendLogin(message, newMachine);
        try {
            sessionID = replay.getMessage();
            //add check
            if (sessionID != null) {
                return true;
                //add decipher
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    public int createBook(String title) {
        String messageContent;
        messageContent = "type:" + "createBook:" + "sessionID:" + sessionID + "bookTitle:" + title;
        SecretKey secretKey = cypherUtil.generateSecretKey();
        String cipheredKey = cypherUtil.cypherSecretKey(password, salt, secretKey);
        Message replay = createBookMessage(messageContent, cipheredKey);
        return Integer.valueOf(replay.getMessage());
    }


    public int createBook(String title, Map<String, Integer> authorsAuth) {
        int bookID = createBook(title);
        if (!authorsAuth.isEmpty()) {
            addAuthorsAuth(String.valueOf(bookID), authorsAuth);
        }
        return bookID;
    }

    public String getBook(String bookID) {
        String messageContent;
        messageContent = "type:" + "getBook" + "sessionID:" + sessionID + "bookID:" + bookID;
        Message message = new Message(messageContent, "");
        Message replay = getBookMessage(message);

        String[] array = replay.getMessage().split("(sendBook:|tempKey:)");

        if (array.length == 3) {
            if (!array[1].isEmpty()) {
                String keyString = array[2];

                keyString = cypherUtil.decypherBookKey(keyString);
                currentBookKey = cypherUtil.stringToKey(keyString);

                String bookContent = cypherUtil.decypherBook(array[1], currentBookKey);
                String cipheredKey = cypherUtil.cypherSecretKey(password, salt, currentBookKey);
                cipheredKey = cypherUtil.cypherMessage(cipheredKey);
                Message secretKey = new Message(cipheredKey, cypherUtil.getSignature(cipheredKey));


                sendMessage(secretKey);
                return bookContent;
            }
        }

        array = replay.getMessage().split("(sendBook:|key:)");

        if (array.length == 3) {
            currentBookKey = cypherUtil.decypherSecretKey(array[2], password, salt);
            if (!array[1].isEmpty()) {
                String bookContent = cypherUtil.decypherBook(array[1], currentBookKey);
                return bookContent;
            }
        }


        return null;
    }

    public Message getBookMessage(Message message) {
        try {
            message.setMessage(cypherUtil.cypherMessage(message.getMessage()));
            message.setSignature(cypherUtil.getSignature(message.getMessage()));
            Socket clientSocket = new Socket("localhost", 8005);
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                return replay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean sendBookChanges(String bookID, String bookContent) {
        String messageContent;
        bookContent = cypherUtil.cypherBook(bookContent, currentBookKey);
        messageContent = "type:" + "receiveBookChanges" + "sessionID:" + sessionID
                + "bookID:" + bookID;
        Message message = new Message(messageContent, "");
        Message replay = sendBookChangesMessage(messageContent, bookContent);

        if (Boolean.valueOf(replay.getMessage())) {
            return true;
        }
        return false;
    }

    public Message sendBookChangesMessage(String messageContent, String bookContent) {
        try {
            Message message = new Message(messageContent, "");
            Message book = new Message(bookContent, cypherUtil.getSignature(bookContent));
            message.setMessage(cypherUtil.cypherMessage(message.getMessage()));
            message.setSignature(cypherUtil.getSignature(message.getMessage()));
            Socket clientSocket = new Socket("localhost", 8005);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            objectOut.writeObject(book);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
                return replay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Book> getBookList() {
        String messageContent;
        messageContent = "type:getBookListsessionID:" + sessionID;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        //add decipher
        String[] bookListString = replay.getMessage().split("book(ID:|Title:)");
        ArrayList<Book> bookList = new ArrayList<>();
        for (int i = 1; i < bookListString.length; i += 2) {
            Book book = new Book(Integer.parseInt(bookListString[i]), bookListString[i + 1]);
            bookList.add(book);
        }
        return bookList;
    }


    public Boolean addAuthorsAuth(String bookID, Map<String, Integer> authorsAuth) {
        String messageContent;
        messageContent = "type:addAuthorsAuthsessionID:" + sessionID + "bookID:" + bookID;
        for (Map.Entry<String, Integer> authorAuth : authorsAuth.entrySet()) {
            messageContent += "username:" + authorAuth.getKey();
            messageContent += "auth:" + authorAuth.getValue();
        }

        Boolean result = sendAuthorAuth(messageContent, authorsAuth);

        return result;
    }

    public Boolean sendAuthorAuth(String messageContent, Map<String, Integer> authorAuth) {
        try {
            messageContent = cypherUtil.cypherMessage(messageContent);
            String signature = cypherUtil.getSignature(messageContent);
            Message message = new Message(messageContent, signature);
            Socket clientSocket = new Socket("localhost", 8005);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());

            SecretKey symKey;
            Message secretKey = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(secretKey.getMessage(), secretKey.getSignature())) {
                secretKey.setMessage(cypherUtil.decypherMessage(secretKey.getMessage()));
                symKey = cypherUtil.decypherSecretKey(secretKey.getMessage(), password, salt);
                for (String key : authorAuth.keySet()) {
                    Message publicKey = (Message) objectIn.readObject();
                    if (cypherUtil.verifySignature(publicKey.getMessage(), publicKey.getSignature())) {
                        String cipheredKey = cypherUtil.cipherBookKey(symKey, publicKey.getMessage());
                        String keySignature = cypherUtil.getSignature(cipheredKey);
                        Message secretMessage = new Message(cipheredKey, keySignature);
                        objectOut.writeObject(secretMessage);
                    }
                }

            }

            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
                return Boolean.valueOf(replay.getMessage());
            }
            objectOut.close();
            objectIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean authorExists(String username) {
        String messageContent;
        messageContent = "type:authorExistsusername:" + username;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageExists(message);
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
        }
        return false;
    }

    public List<String> getAuthorsFromBook(String bookID) {
        String messageContent;
        messageContent = "type:" + "getAuthorsFromBook" + "sessionID:" + sessionID + "bookID:" + bookID;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        String[] authors = replay.getMessage().split("username:");
        List<String> authorsList = new ArrayList<>();
        for (int i = 1; i < authors.length; i++) {
            authorsList.add(authors[i]);
        }
        return authorsList;
    }

    public int getAuthFromAuthor(String bookID, String username) {
        String messageContent;
        messageContent = "type:" + "getAuthFromBook" + "sessionID:" + sessionID + "bookID:" + bookID
                + "username:" + username;
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);

        return Integer.valueOf(replay.getMessage());
    }

    public Message sendLogin(Message message, Boolean newMachine) {
        try {
            message.setMessage(cypherUtil.cypherMessage(message.getMessage()));
            Socket clientSocket = new Socket("localhost", 8005);
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            if (newMachine) {
                Message secret = (Message) objectIn.readObject();
                if (cypherUtil.verifySignature(secret.getMessage(), secret.getSignature())) {
                    cypherUtil.decipherPrivate(username, password, salt, secret.getMessage());
                }

            }
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
                return replay;
            }
            objectOut.close();
            objectIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message createBookMessage(String bookInfo, String secretKey) {
        try {
            String cypheredBookInfo = cypherUtil.cypherMessage(bookInfo);
            String cypheredKey = cypherUtil.cypherMessage(secretKey);
            Socket clientSocket = new Socket("localhost", 8005);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            Message message = new Message(cypheredBookInfo, cypherUtil.getSignature(cypheredBookInfo));
            Message secretKeyMessage = new Message(cypheredKey, cypherUtil.getSignature(cypheredKey));
            objectOut.writeObject(message);
            objectOut.writeObject(secretKeyMessage);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
                return replay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean removeAuthor(String bookID, String username) {

        String content = getBook(bookID);

        List<String> authors = getAuthorsFromBook(bookID);

        authors.remove(username);

        String messageContent = "type:" + "removeAuthor" + "sessionID:" + sessionID +
                "bookID:" + bookID + "username:" + username;

        Message request = new Message(cypherUtil.cypherMessage(messageContent), "");

        request.setSignature(cypherUtil.getSignature(request.getMessage()));

        try {
            Socket clientSocket = new Socket("localhost", 8005);

            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(request);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());

            SecretKey secretKey = cypherUtil.generateSecretKey();
            for (String author : authors) {
                Message publicKey = (Message) objectIn.readObject();
                if (cypherUtil.verifySignature(publicKey.getMessage(), publicKey.getSignature())) {
                    String cipheredKey = cypherUtil.cipherBookKey(secretKey, publicKey.getMessage());
                    String keySignature = cypherUtil.getSignature(cipheredKey);
                    Message secretMessage = new Message(cipheredKey, keySignature);
                    objectOut.writeObject(secretMessage);
                }
            }
            Message bookNewCipher;
            if (content != null) {
                bookNewCipher = new Message(cypherUtil.cypherBook(content, secretKey), "");
            } else {
                bookNewCipher = new Message("", "");
            }
            bookNewCipher.setSignature(cypherUtil.getSignature(bookNewCipher.getMessage()));

            objectOut.writeObject(bookNewCipher);

            String cipheredKey = cypherUtil.cypherSecretKey(password, salt, secretKey);

            Message cipheredKeyMessage = new Message(cipheredKey, cypherUtil.getSignature(cipheredKey));

            objectOut.writeObject(cipheredKeyMessage);

            Message success = (Message) objectIn.readObject();

            if (cypherUtil.verifySignature(success.getMessage(), success.getSignature())) {
                success.setMessage(cypherUtil.decypherMessage(success.getMessage()));
                return Boolean.valueOf(success.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void sendMessage(Message message) {
        try {
            objectOut.writeObject(message);
            objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message sendMessageExists(Message message) {
        try {
            message.setMessage(cypherUtil.cypherMessage(message.getMessage()));
            Socket clientSocket = new Socket("localhost", 8005);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                return replay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message sendMessageReplay(Message message) {
        try {
            message.setMessage(cypherUtil.cypherMessage(message.getMessage()));
            message.setSignature(cypherUtil.getSignature(message.getMessage()));
            Socket clientSocket = new Socket("localhost", 8005);
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(message);
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            if (cypherUtil.verifySignature(replay.getMessage(), replay.getSignature())) {
                replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
                return replay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message createUserMessage(String messageContent, String secretString, String publicKeyString, SecretKey macKey) {
        try {
            Message messageAccount = new Message(messageContent, "");

            String secretMac = cypherUtil.MAC(secretString, macKey);

            Message secret = new Message(secretString, secretMac);


            String publicKeyMac = cypherUtil.MAC(publicKeyString, macKey);

            Message publicKey = new Message(publicKeyString, publicKeyMac);

            Socket clientSocket = new Socket("localhost", 8005);
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(messageAccount);
            objectOut.writeObject(secret);
            objectOut.writeObject(publicKey);
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Message replay = (Message) objectIn.readObject();
            return replay;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean logout() {
        String messageContent = "type:" + "logout" + "sessionID:" + sessionID;

        Message message = new Message(messageContent, "");

        Message replay = sendMessageReplay(message);

        if (replay != null) {
            return Boolean.valueOf(replay.getMessage());
        }

        return false;
    }

}
