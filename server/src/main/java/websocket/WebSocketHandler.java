package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import records.UserGameCommandRecord;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // This class is modeled after the sample code provided in the PetShop example.

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler() {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommandRecord command = new Gson().fromJson(message, UserGameCommandRecord.class);
        switch (command.userGameCommand().getCommandType()) {
            case CONNECT -> join(command.givenUser(), session);
            case MAKE_MOVE -> {
                //have the make move logic
            }
            case LEAVE -> {
                //have the leave logic
            }
            case RESIGN -> {
                //have the resign logic
            }
            case OBSERVE -> observe(command.givenUser(), session);
            case null, default -> {
                //throw something
            }
            // in here you make the different things do different things
        }
    }

    private void join(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("User '%s' has joined the game.", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(username, serverMessage);
    }

    private void observe(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("User '%s' is now observing the game.", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(username, serverMessage);
    }

}
