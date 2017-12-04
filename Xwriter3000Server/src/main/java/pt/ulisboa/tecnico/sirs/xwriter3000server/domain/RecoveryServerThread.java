package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RecoveryServerThread extends Thread {

    private Socket clientSocket;
    private MessageParser parser;
    private Message message;
    private boolean mainServerDown;

    public RecoveryServerThread(Socket brother) {
        this.clientSocket = brother;
        this.parser = new MessageParser();
        mainServerDown = false;
    }

            public void run() {
                try {
                    ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
                    //message = (Message) CypherUtil.decypher(stream.readObject());
                    message = (Message) inFromClient.readObject();
                    message = parser.parseType(message);
                    switch (message.getType()) {
                        case "alarm":
                            checkAlarm(message);
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Problem1");
                } catch (ClassNotFoundException e) {
                    System.out.println("Problem2");
                }catch (ClassCastException e){
                    System.out.println("Problem3");
                } finally {
                    try{
                        clientSocket.close();
                    }catch (IOException e){
                        System.out.println("Problem4");
                    }
                }
            }


    public void checkAlarm(Message message){
        Date alarmDate = parser.parseAlarm(message.getMessage());
        Date currentDate = new Date();
        long timeDifference = currentDate.getTime() - alarmDate.getTime();
        System.out.println("timeDifference = " + timeDifference);
        if (timeDifference > 5000) {
            System.out.println("Main server down.");
            mainServerDown = true;
        }
    }

    public boolean getMainServerDown(){
        return mainServerDown;
    }
}
