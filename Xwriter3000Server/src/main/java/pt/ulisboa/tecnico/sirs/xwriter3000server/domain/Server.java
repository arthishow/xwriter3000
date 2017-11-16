package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    CommunicationServer communicationServer;

    ServerSocket serverSocket;

    public Server(int port) throws Exception {
        communicationServer = new CommunicationServer();
        serverSocket = new ServerSocket(port);
    }

    public void run() throws Exception{

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket, communicationServer).start();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
        }

    }
}
