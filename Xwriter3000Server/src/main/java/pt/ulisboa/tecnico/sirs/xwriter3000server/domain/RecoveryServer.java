package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RecoveryServer {

    private CommunicationServer communicationServer;
    private ServerSocket serverSocket;
    private String brotherIp;
    private int brotherPort;
    private MessageParser parser;
    private boolean recoveryMode;

    public RecoveryServer(String brotherIp, int brotherPort) throws Exception{
        this.communicationServer = new CommunicationServer();
        this.brotherIp = brotherIp;
        this.brotherPort = brotherPort;
        this.serverSocket = new ServerSocket();
        this.parser = new MessageParser();
        this.recoveryMode = true;
    }

    public void run() throws Exception {
        serverSocket.bind(new InetSocketAddress(brotherIp, brotherPort));
        serverSocket.setSoTimeout(15000);
        while(recoveryMode){
            try {
                Socket brother = serverSocket.accept();
                RecoveryServerThread t = new RecoveryServerThread(brother);
                new Thread(t).start();
                t.join();
                if(t.getMainServerDown()){
                    switchServer();
                }
            }catch (SocketTimeoutException e){
                System.out.println("Timed out");
                switchServer();
            }
        }

        Server server = new Server(8001, null, -1);
        server.run();

    }

    public void switchServer(){
        System.out.println("Let's switch servers");
        recoveryMode = false;
    }
}
