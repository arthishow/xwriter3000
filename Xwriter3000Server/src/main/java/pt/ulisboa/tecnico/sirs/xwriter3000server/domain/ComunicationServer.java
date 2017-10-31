package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ComunicationServer {

    private int port;

    private List activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());

    public ComunicationServer(int port){
        this.port = port;
    }
}
