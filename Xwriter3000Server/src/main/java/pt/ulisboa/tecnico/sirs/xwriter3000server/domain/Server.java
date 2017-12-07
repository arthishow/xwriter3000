package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    private CommunicationServer communicationServer;
    private ServerSocket serverSocket;
    private CypherUtil cypherUtil;
    private String brotherIp;
    private int brotherPort;


    public Server(int port, String brotherIp, int brotherPort) throws Exception {
        this.cypherUtil = new CypherUtil();
        this.serverSocket = new ServerSocket(port);
        this.communicationServer = new CommunicationServer(cypherUtil);
        this.brotherIp = brotherIp;
        this.brotherPort = brotherPort;
    }

    public Server(int port) throws Exception {
        this.cypherUtil = new CypherUtil();
        this.serverSocket = new ServerSocket(port);
        this.communicationServer = new CommunicationServer(cypherUtil);
        this.brotherIp = null;
        this.brotherPort = -1;
    }


    public void run() throws Exception {
        if (brotherPort != -1 && brotherIp != null) {
            alertEveryGivenSeconds(5);
        }

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

    private void alertEveryGivenSeconds(int seconds) {
        Runnable alive = new Runnable() {
            public void run() {
                try {
                    Socket brotherSocket = new Socket(brotherIp, brotherPort);
                    ObjectOutputStream outToClient = new ObjectOutputStream(brotherSocket.getOutputStream());
                    String alarm = "type:alarm:";
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    alarm = alarm.concat(timeStamp);
                    //timeStamp = CypherUtil.cypherAndSign(alarm);
                    System.out.println("Just sent " + alarm);
                    Message message = new Message(alarm, "");
                    outToClient.writeObject(message);
                } catch (Exception e) {
                    System.out.println("Problem sending " + e.getMessage());
                }
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(alive, 0, seconds, TimeUnit.SECONDS);
    }
}
