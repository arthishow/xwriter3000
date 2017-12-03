package pt.ulisboa.tecnico.sirs.xwriter3000client;


import com.sun.org.apache.xpath.internal.operations.Bool;
import pt.ulisboa.tecnico.sirs.xwriter3000.Message;
import pt.ulisboa.tecnico.sirs.xwriter3000ui.Book;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

public class CommunicationClient {

    private String sessionID;

    private CypherUtil cypherUtil;

    private String salt;

    public CommunicationClient() {
        cypherUtil = new CypherUtil();
        cypherUtil.readServerPublicKey();
    }

    public Boolean createUser(String username, String password){
        List<String> keyPair = cypherUtil.generateKeyPair();

        salt = cypherUtil.generateSalt();
        System.out.println(salt);

        SecretKey macKey = cypherUtil.generateMac();
        String encodedMacKey = Base64.getEncoder().encodeToString(macKey.getEncoded());



        String messageContent;
        messageContent = "type:" + "createUser" + "username:" + username + "password:" + password + "MAC:" + encodedMacKey;

        messageContent = cypherUtil.cypherMessage(messageContent);
        Message messageAccount = new Message(messageContent, "");

        String secretString = cypherUtil.cipherPrivate(password, salt,keyPair.get(1));

        String secretMac = cypherUtil.MAC(secretString, macKey);

        Message secret = new Message(secretString, secretMac);

        String publicKeyString = keyPair.get(0);

        String publicKeyMac = cypherUtil.MAC(publicKeyString, macKey);

        Message publicKey = new Message(publicKeyString, publicKeyMac);

        Message replay = loginMessage(messageAccount, secret, publicKey);
        if (Boolean.valueOf(replay.getMessage())) {
            return true;
                //add decipher
        }
        return false;
    }

    //fixme
    public Boolean authenticateUser(String username, String password) {
        String messageContent;
        messageContent = "type:" + "authenticateUser" + "username:" + username + "password:" + password
                            + "newMachine:" ;
        messageContent = cypherUtil.cypherMessage(messageContent);
        Message message = new Message(messageContent, "");
        Message replay = sendMessageReplay(message);
        replay.setMessage(cypherUtil.decypherMessage(replay.getMessage()));
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
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message loginMessage(Message messageAccount, Message secret, Message publicKey) {
        try{
            Socket clientSocket = new Socket("localhost", 8001);
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


}
