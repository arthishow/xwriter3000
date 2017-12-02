package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    CommunicationServer communicationServer;
    ServerSocket serverModeSocket;
    Socket brotherSocket;
    boolean recoveryServer;
    String brotherIp;
    int brotherPort;

    public Server(int port, String brotherIp, int brotherPort, boolean recoveryServer) throws Exception {
        this.communicationServer = new CommunicationServer();
        this.serverModeSocket = new ServerSocket(port);
        this.brotherIp = brotherIp;
        this.brotherPort = brotherPort;
        this.recoveryServer = recoveryServer;
    }

    public void run() throws Exception{
        if(!recoveryServer){
           // brotherSocket = new Socket(brotherIp, brotherPort);
            alertEveryGivenSeconds(5);
        }else{
            brotherSocket = new Socket();
            brotherSocket.bind(new InetSocketAddress(brotherIp, brotherPort));
        }

        while (true) {
            if(recoveryServer) {
                try{
                    new RecoveryServerThread(brotherSocket).start();
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            }else {
                try {
                    Socket clientSocket = serverModeSocket.accept();
                    new ServerThread(clientSocket, communicationServer).start();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                }
                // new thread for a client
            }
        }
    }

    private void alertEveryGivenSeconds(int seconds){
        Runnable alive = new Runnable() {
            public void run() {
                try {
                    ObjectOutputStream outToClient = new ObjectOutputStream(brotherSocket.getOutputStream());
                    String alarm = "type:alarm:";
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    //timeStamp = CypherUtil.cypherAndSign(alarm.concat(timeStamp));
                    System.out.print("Just sent " + timeStamp);
                    outToClient.writeObject(timeStamp);
                } catch (IOException e) {
                    System.out.println("Problem");
                }
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(alive, 0, seconds, TimeUnit.SECONDS);
    }
}
