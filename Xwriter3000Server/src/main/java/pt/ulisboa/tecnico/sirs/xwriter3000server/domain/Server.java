package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    ServerSocket serverSocket;
    int port;
    CommunicationServer communicationServer;
    String brotherIp;
    int brotherPort;
    Socket brotherSocket;

    public Server(int port, String brotherIp, int brotherPort) throws Exception {
        this.serverSocket = new ServerSocket(8001);
        this.port = port;
        this.communicationServer = new CommunicationServer();
        this.brotherIp = brotherIp;
        this.brotherPort = brotherPort;
        this.brotherSocket = new Socket(brotherIp, 8002);
    }

    public void run() throws Exception{
        System.out.println("Sisi");
        alertEveryGivenSeconds(5);

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

    private void alertEveryGivenSeconds(int seconds){
        Runnable alive = new Runnable() {
            public void run() {
                try {
                        ObjectOutputStream outToClient = new ObjectOutputStream(brotherSocket.getOutputStream());
                        String alarm = "type:alarm:";
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                        alarm = alarm.concat(timeStamp);
                        //timeStamp = CypherUtil.cypherAndSign(alarm);
                        System.out.print("Just sent " + alarm);
                        outToClient.writeObject(alarm);

                } catch (Exception e) {
                    System.out.println("Problem");
                }
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(alive, 0, seconds, TimeUnit.SECONDS);
    }
}
