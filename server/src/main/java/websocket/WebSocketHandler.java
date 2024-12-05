package websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccessException;
import dataaccess.*;
//import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
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
    //private GameData currGame;

    public WebSocketHandler(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommandRecord command = new Gson().fromJson(message, UserGameCommandRecord.class);
        switch (command.userGameCommand().getCommandType()) {
            case CONNECT -> {
                //isPlaying
                connect(command.givenUser(), session);
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

    @OnWebSocketError
    public void tellMeWhatsUp(java.lang.Throwable throwable) {
        //fixme
        System.out.println("There was a freaking error");
        System.out.println(throwable.getMessage());
    }

    //boolean isPlaying, String playerColor, GameData game
    private void connect(String username, Session session) throws IOException {
        // include the color! update for that
        // fixme server should send a load_game message back to the root client here
        connections.add(username, session);
//        //this.currGame = game;
//        String message = "";
//        if(isPlaying) {
//            message = String.format("User '%s' has joined the game.", username); // playing as %s, playerColor
//        } else {
//            message = String.format("User '%s' is now observing the game.", username);
//        }
        // can delete this dude vv later
        var message = String.format("User '%s' has joined the game.", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
        // was a NotificationMessage
        //TEST??
        serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
    }

    // and then here was the observe function, but,,,

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

    //, boolean isPlaying, String playerColor
    private void leave(String username, Session session) throws IOException {
        //fixme: game updated to remove root client; game updated in database
        var message = String.format("User '%s' has left the game.", username);
        //        String message = "";
//        if(isPlaying) {
//            GameData newGame;
//            //here is where you do the dataAccess stuff
//            if(playerColor.equalsIgnoreCase("white")) {
//                newGame = new GameData(currGame.gameID(), null, currGame.blackUsername(), currGame.gameName(), currGame.game());
//            } else {
//                //might need to make sure bad things don't happen here
//                newGame = new GameData(currGame.gameID(), currGame.whiteUsername(), null, currGame.gameName(), currGame.game());
//            }
//            dataAccess.updateGame(currGame.gameID(), newGame);
//            message = String.format("Player '%s' (%s) has left the game.", username, playerColor.toLowerCase());
//        } else {
//            // if not playing, then no change must be made to the game
//            message = String.format("Observer '%s' has left the game.", username);
//        }
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
        //connections.remove(username);
    }

    private void resign(String username, Session session) throws IOException {
        // resignation message
        // fixme: server marks game as over; game updated in database; message to ALL clients that root client resigned
    }

    //FIXME notifs for check and checkmate (player name)

}
