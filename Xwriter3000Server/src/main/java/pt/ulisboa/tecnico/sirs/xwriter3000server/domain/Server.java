package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    CommunicationServer communicationServer;

    ServerSocket serverSocket;

    CypherUtil cypherUtil;

    public Server(int port) throws Exception {
        cypherUtil = new CypherUtil();
        serverSocket = new ServerSocket(port);
        communicationServer = new CommunicationServer(cypherUtil);
    }

    public void run() throws Exception{
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket, communicationServer, cypherUtil).start();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
        }

    }
}
