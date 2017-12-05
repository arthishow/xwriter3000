package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

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
                    adviseFirewall();
                }
            }catch (SocketTimeoutException e){
                System.out.println("Timed out");
                switchServer();
                adviseFirewall();
            }
        }
        serverSocket.close();
        Server server = new Server(brotherPort);
        server.run();

    }

    public void switchServer(){
        System.out.println("Let's switch servers");
        recoveryMode = false;
    }

    public void adviseFirewall(){
        try {
            Socket firewall = new Socket("firewall ip", 8003);
            Message message = new Message("type:switch", "");
            ObjectOutputStream objectOut = new ObjectOutputStream(firewall.getOutputStream());
            objectOut.writeObject(message);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
