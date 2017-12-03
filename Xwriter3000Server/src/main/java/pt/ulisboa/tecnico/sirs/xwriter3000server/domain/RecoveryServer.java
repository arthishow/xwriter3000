package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RecoveryServer {

    CommunicationServer communicationServer;
    Socket serverSocket;
    String brotherIp;
    int brotherPort;
    private MessageParser parser;
    private int counter;

    public RecoveryServer(String brotherIp, int brotherPort) throws Exception {
        this.communicationServer = new CommunicationServer();
        this.brotherIp = brotherIp;
        this.brotherPort = brotherPort;
        this.serverSocket = new Socket();
        this.parser = new MessageParser();
        this.counter = 0;
    }

    public void run() throws Exception {
        serverSocket.bind(new InetSocketAddress(brotherIp, brotherPort));
        checkEveryGivenSeconds(5);
    }

    private void checkEveryGivenSeconds(int seconds){
        Runnable alive = new Runnable() {
            public void run() {
                try {
                    ObjectInputStream inFromClient = new ObjectInputStream(serverSocket.getInputStream());
                    //message = (Message) CypherUtil.decypher(stream.readObject());
                    Message message = (Message) inFromClient.readObject();
                    message = parser.parseType(message);
                    switch (message.getType()) {
                        case "alarm":
                            checkAlarm(message);
                            counter = 0;
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Problem1");
                    counter++;
                } catch (ClassNotFoundException e) {
                    System.out.println("Problem2");
                }
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(alive, 0, seconds, TimeUnit.SECONDS);
    }

    public void checkAlarm(Message message){
        Date alarmDate = parser.parseAlarm(message.getMessage());
        Date currentDate = new Date();
        long timeDifference = currentDate.getTime() - alarmDate.getTime();
        if (timeDifference > 5000) {
            System.out.println("Main server down.");
        }
    }
}
