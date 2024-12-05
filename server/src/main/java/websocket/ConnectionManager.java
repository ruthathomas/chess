package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // This class is modeled after the sample code provided in the PetShop example.

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    //include all vars for making the connection
    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludedUser, ServerMessage serverMessage) throws IOException {
        String message = ">:(";
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> message = ((NotificationMessage) serverMessage).getMessage();
            case ERROR -> message = ((ErrorMessage) serverMessage).getErrorMessage();
            case LOAD_GAME -> message = ((LoadGameMessage) serverMessage).toString();
        }
        var unusedConnList = new ArrayList<Connection>();
        for(var connection : connections.values()) {
            if(connection.session.isOpen()) {
                if(!connection.username.equals(excludedUser)) {
                    connection.send(message);
                }
            } else {
                unusedConnList.add(connection);
            }
        }
        for(var connection : unusedConnList) {
            connections.remove(connection.username);
        }
    }

}
