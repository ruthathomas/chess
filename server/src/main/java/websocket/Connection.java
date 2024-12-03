package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    // This class is modeled after the sample code in the PetShop example.

    //put some variables here ig
    public String username;
    public Session session;

    public Connection(String username, Session session) {
        //initialize the variables here
        this.username = username;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

}
