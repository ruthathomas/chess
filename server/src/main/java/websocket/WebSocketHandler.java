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
    public void onMessage(Session session, String message) throws IOException {
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
                resign(command.givenUser());
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
        try {
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
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.broadcastSelf(username, serverMessage);
        }
    }

    // and then here was the observe function, but,,,

    private void makeMove(String username, int gameID, GameData game, String move) throws IOException {
        //fixme server sends load_game message to all clients, plus updates boards
        // if a bad move is requested, this whole thing crashes. fix that --> TRY CATCH THIS SUCKER
        try {
            String message = "";
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
            connections.broadcast(null, serverMessage);
            dataAccess.updateGame(gameID, game);
            message = String.format("Player '%s' moved %s.", username, move);
            String statusMessage = getGameStatusMessage(game);
            message += " " + getGameStatusMessage(game);
            // fixme there's probably a better way to do this; server sends check, checkmate, or stalemate notif if caused
            if (message.contains("WHITE is in")) {
                if(message.contains("checkmate") || message.contains("stalemate")) {
                    dataAccess.endGame(game);
                    message += "Team BLACK has won. Thank you for playing!";
                }
            } else if(message.contains("BLACK is in")) {
                if(message.contains("checkmate") || message.contains("stalemate")) {
                    dataAccess.endGame(game);
                    message += "Team WHITE has won. Thank you for playing!";
                }
            }
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username, serverMessage);
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.broadcastSelf(username, serverMessage);
        }
    }

    //there are some issues with leaving; it doesn't always work??
    private void leave(String username, boolean isPlaying, String playerColor, GameData game) throws IOException{
        try {
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
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.broadcastSelf(username, serverMessage);
        }
    }

    private void resign(String username) throws IOException {
        try {
            dataAccess.endGame(currGame);
            dataAccess.updateGame(currGame.gameID(), currGame);
            String message =String.format("Player '%s' has resigned from the game. Thank you for playing!", username);
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, serverMessage);
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.broadcastSelf(username, serverMessage);
        }
    }

    private String getGameStatusMessage(GameData game) {
        // why the string concatenation??? change to if/else
        String message = "";
        String username;
        for(var color : ChessGame.TeamColor.values()) {
            if(color.toString().equalsIgnoreCase("white")) {
                username = game.whiteUsername();
            } else {
                username = game.blackUsername();
            }
            if(game.game().isInCheck(color)) {
                message += String.format("Player '%s' (%s) is in check. ", username, color.toString());
            }
            if(game.game().isInCheckmate(color)) {
                message += String.format("Player '%s' (%s) is in checkmate. ", username, color.toString());
            }
            if(game.game().isInStalemate(color)) {
                message += String.format("Player '%s' (%s) is in check. ", username, color.toString());
            }
        }
        return message;
    }

}
