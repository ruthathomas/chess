package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessInterface;
import dataaccess.MemoryDataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import records.UserGameCommandRecord;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // MAKE THIS TAKE A DATA ACCESS OBJECT SO YOU CAN MAKE CHANGES HERE

    // This class is modeled after the sample code provided in the PetShop example.

    private final ConnectionManager connections = new ConnectionManager();

    private DataAccessInterface dataAccess = new MemoryDataAccess();

    public WebSocketHandler(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommandRecord command = new Gson().fromJson(message, UserGameCommandRecord.class);
        switch (command.userGameCommand().getCommandType()) {
            case CONNECT -> {
                connect(command.givenUser(), session, command.isPlaying());
            }
            case MAKE_MOVE -> {
                //have the make move logic; should take ChessMove move
            }
            case LEAVE -> leave(command.givenUser(), session);
            case RESIGN -> {
                //have the resign logic (ends game)
            }
            case null, default -> {
                //throw something
            }
            // in here you make the different things do different things
        }
    }

    private void connect(String username, Session session, boolean isPlaying) throws IOException {
        // include the color! update for that
        // fixme server should send a load_game message back to the root client here
        connections.add(username, session);
        String message = "";
        if(isPlaying) {
            message = String.format("User '%s' has joined the game.", username);
        } else {
            message = String.format("User '%s' is now observing the game.", username);
        }
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
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
        //FIXME follow these directions
        //server verifies validity of move; game updates to represnt the move; game updated in database
        //server sends load_game message to all clients
        // server sends notif to all OTHER clients informing them of made move
        // server sends check, checkmate, or stalemate notif if caused
    }

    private void leave(String username, Session session) throws IOException {
        //fixme: game updated to remove root client; game updated in database
        var message = String.format("User '%s' has left the game.", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
    }

    private void resign(String username, Session session) throws IOException {
        // resignation message
        // fixme: server marks game as over; game updated in database; message to ALL clients that root client resigned
    }

    //FIXME notifs for check and checkmate (player name)

}
