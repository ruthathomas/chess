package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import records.UserGameCommandRecord;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
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
                //have the make move logic; should take ChessMove move
            }
            case LEAVE -> leave(command.givenUser(), session);
            case RESIGN -> {
                //have the resign logic (ends game)
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
        // include the color! update for that
        var message = String.format("User '%s' has joined the game.", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, serverMessage);
    }

    private void makeMove(String username, Session session) throws IOException {
        // message should include the player's name and descr of move made, plus board should update
        //example of what the serialization should look like roughly
//        {
//            "commandType": "MAKE_MOVE",
//                "authToken": "tokengoeshere",
//                "gameID": "337",
//                "move": { "start": { "row": 3, "col": 3 }, "end": { "row": 5, "col": 5 } }
//        }
    }

    private void leave(String username, Session session) throws IOException {
        var message = String.format("User '%s' has left the game.", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, serverMessage);
    }

    private void resign(String username, Session session) throws IOException {
        // resignation message
    }

    //maybe you should change this bc idk if you should've changed the enum
    private void observe(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("User '%s' is now observing the game.", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, serverMessage);
    }

    //FIXME notifs for check and checkmate (player name)

}
