package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.*;
//import model.GameData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import records.UserGameCommandRecord;
//import websocket.messages.NotificationMessage;
import server.Server;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // MAKE THIS TAKE A DATA ACCESS OBJECT SO YOU CAN MAKE CHANGES HERE

    // This class is modeled after the sample code provided in the PetShop example.

    private final ConnectionManager connections = new ConnectionManager();

    private DataAccessInterface dataAccess = new MemoryDataAccess();
    private GameData currGame;

    public WebSocketHandler(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommandRecord command = new Gson().fromJson(message, UserGameCommandRecord.class);
        switch (command.userGameCommand().getCommandType()) {
            case CONNECT -> {
                //isPlaying
                connect(command.givenUser(), session, command.isPlaying(), command.playerColor(), command.game());
            }
            case MAKE_MOVE -> {
                //have the make move logic; should take ChessMove move
                makeMove(command.givenUser(), command.userGameCommand().getGameID(), command.game(), command.move());
            }
            case LEAVE -> leave(command.givenUser(), command.isPlaying(), command.playerColor(),command.game());
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
        System.out.println("\nThere was a freaking error\n");
        System.out.println(throwable.getMessage() + "\n");
    }

    //boolean isPlaying, String playerColor, GameData game
    private void connect(String username, Session session, boolean isPlaying, String playerColor, GameData game)
            throws IOException {
        // fixme server should send a load_game message back to the root client here; also, figure out with the
        //concatenation of the strings and fix it up :( ; also, probably extract to its own function
        connections.add(username, session);
        String message = getGameStatusMessage(game);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcastSelf(username,serverMessage);
        currGame = game;
        if(isPlaying) {
            message = String.format("User '%s' has joined the game playing as %s.", username, playerColor);
        } else {
            message = String.format("User '%s' is now observing the game.", username);
        }
        serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
    }

    // and then here was the observe function, but,,,

    private void makeMove(String username, int gameID, GameData game, String move) throws IOException, DataAccessException {
        //fixme server sends load_game message to all clients, plus updates boards
        // if a bad move is requested, this whole thing crashes. fix that --> TRY CATCH THIS SUCKER
        String message = "";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(null, serverMessage);
        dataAccess.updateGame(gameID, game);
        message = String.format("Player '%s' moved %s.", username, move);
        String statusMessage = getGameStatusMessage(game);
        message += " " + getGameStatusMessage(game);
        //fixme there's probably a better way to do this
        if (message.contains("Team WHITE is in")) {
            if(message.contains("checkmate") || message.contains("stalemate")) {
                dataAccess.endGame(game);
                message += "Team BLACK has won. Thank you for playing!";
            }
        } else if(message.contains("Team BLACK is in")) {
            if(message.contains("checkmate") || message.contains("stalemate")) {
                dataAccess.endGame(game);
                message += "Team WHITE has won. Thank you for playing!";
            }
        }
        serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
        // fixme server sends check, checkmate, or stalemate notif if caused
    }

    //there are some issues with leaving; it doesn't always work?? also, it seems to have reset?? try again
    private void leave(String username, boolean isPlaying, String playerColor, GameData game) throws IOException, DataAccessException {
        String message = "";
        if(isPlaying) {
            GameData newGame;
            if(playerColor.equalsIgnoreCase("white")) {
                newGame = new GameData(currGame.gameID(), null, currGame.blackUsername(), currGame.gameName(), game.game(), currGame.isOver());
            } else {
                //might need to make sure bad things don't happen here
                newGame = new GameData(currGame.gameID(), currGame.whiteUsername(), null, currGame.gameName(), game.game(), currGame.isOver());
            }
            dataAccess.updateGame(currGame.gameID(), newGame);
            message = String.format("Player '%s' (%s) has left the game.", username, playerColor);
        } else {
            // if not playing, then no change must be made to the game
            message = String.format("Observer '%s' has left the game.", username);
        }
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, serverMessage);
        //connections.remove(username);
    }

    private void resign(String username, Session session) throws IOException, DataAccessException {
        // resignation message
        // fixme: server marks game as over; game updated in database; message to ALL clients that root client resigned
        dataAccess.endGame(currGame);
        dataAccess.updateGame(currGame.gameID(), currGame);
        String message =String.format("Player '%s' has resigned from the game. Thank you for playing!", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, serverMessage);
    }

    //FIXME notifs for check and checkmate (player name)

    private String getGameStatusMessage(GameData game) {
        String message = "";
        for(var color : ChessGame.TeamColor.values()) {
            if(game.game().isInCheck(color)) {
                message += String.format("Team %s is in check. ", color.toString());
            }
            if(game.game().isInCheckmate(color)) {
                message += String.format("Team %s is in checkmate. ", color.toString());
            }
            if(game.game().isInStalemate(color)) {
                message += String.format("Team %s is in check. ", color.toString());
            }
        }
        return message;
    }

}
