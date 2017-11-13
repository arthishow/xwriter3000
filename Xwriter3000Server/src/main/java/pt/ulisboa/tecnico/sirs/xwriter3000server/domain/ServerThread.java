package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    //FIXME: this function will be refactorized because it is ugly
    public void run(){

        try {
            ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            message = (Message) inFromClient.readObject();
            message = parser.parseType(message);
            switch (message.getType()) {
                case "authenticateUser":
                    List<String> credentials = parser.parseAuthenticateUser(message.getMessage());
                    if (credentials != null) {
                        String sessionID = communicationServer.authenticateUser(credentials.get(0), credentials.get(1));
                        //add cypher
                        Message replay = new Message(sessionID, "");
                        ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                        outToClient.writeObject(replay);
                    }
                    break;
                case "sendBook":
                    List<String> bookInfo = parser.parseSendBook(message.getMessage());
                    if (bookInfo != null) {
                        String book = communicationServer.sendBook(bookInfo.get(0), bookInfo.get(1));
                        //add cypher
                        Message replay = new Message(book, "");
                        ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                        outToClient.writeObject(replay);
                    }
                    break;
                case "receiveBookChanges":
                    List<String> info = parser.parseReceiveBookChanges(message.getMessage());
                    if (info != null){
                        Boolean success = communicationServer.receiveBookChanges(info.get(0), info.get(1));
                        //add cypher
                        Message replay = new Message(success.toString(), "");
                        ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                        outToClient.writeObject(replay);
                    }
                    break;
                case "forwardSymKey":
                    //server.forwardSymKey();
                    break;
                //todo: think this will be needed
                case "register":
                    break;
                case "getBookIDList":
                    break;
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Problem");
        } catch (ClassNotFoundException e){
            System.out.println("Problem with the object class");
        }
    }

}
