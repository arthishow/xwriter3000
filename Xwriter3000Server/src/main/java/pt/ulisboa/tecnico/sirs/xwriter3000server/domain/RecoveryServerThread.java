package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import pt.ulisboa.tecnico.sirs.xwriter3000.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class RecoveryServerThread extends Thread {

        private MessageParser parser;
        private Socket brotherSocket;
        private Message message;

        public RecoveryServerThread(Socket brotherSocket){
            this.parser = new MessageParser();
            this.brotherSocket = brotherSocket;
        }

    public void run(){

        try {
            ObjectInputStream inFromClient = new ObjectInputStream(brotherSocket.getInputStream());
            //message = (Message) CypherUtil.decypher(inFromClient.readObject());
            message = parser.parseType(message);
            switch (message.getType()) {
                case "alarm":
                    checkAlarm(message);
                    break;
            }
        } catch (IOException e) {
            System.out.println("Problem");
        }
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
