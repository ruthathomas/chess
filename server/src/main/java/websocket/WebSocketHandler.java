package websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
//import model.GameData;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
//import websocket.messages.NotificationMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    // MAKE THIS TAKE A DATA ACCESS OBJECT SO YOU CAN MAKE CHANGES HERE

    // This class is modeled after the sample code provided in the PetShop example.

    private final ConcurrentHashMap<Integer, ConnectionManager> connections = new ConcurrentHashMap<>();

    private DataAccessInterface dataAccess = new MemoryDataAccess();
    private GameData currGame;

    public WebSocketHandler(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            if(authData == null) {
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "authentication failed.");
                session.getRemote().sendString(serverMessage.toString());
            } else {
                String requestingUser = authData.username();
                switch (command.getCommandType()) {
                    case CONNECT -> connect(requestingUser, command.getGameID(), session);
                    case MAKE_MOVE -> {
                        //have the make move logic; should take ChessMove move
                        makeMove(requestingUser, command.getGameID(), command.fetchMove(), session);
                    }
                    case LEAVE -> leave(requestingUser, command.getGameID());
                    case RESIGN -> resign(requestingUser, session, command.getGameID());
                    case null, default -> {
                        //throw something
                    }
                    // in here you make the different things do different things
                }
            }
        } catch (Exception ex) {
            //take a look at this again later
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(serverMessage.toString());
        }
    }

    @OnWebSocketError
    public void tellMeWhatsUp(java.lang.Throwable throwable) {
        // probably change this, but it's fine for now
        System.out.println("\nThere was a freaking error\n");
        System.out.println(throwable.getMessage() + "\n");
    }

    private void connect(String username, int gameID, Session session)
            throws IOException {
        try {
            if(!connectionManagerExists(gameID)) {
                connections.put(gameID, new ConnectionManager());
            }
            ConnectionManager currConnections = connections.get(gameID);
            currConnections.add(username, session);
            //connections.add(username, session);
            GameData game = dataAccess.getGame(gameID);
            if(game == null) {
                currConnections.broadcastSelf(username, new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "invalid game ID requested."));
            } else {
                String playerColor = getPlayerColor(username, game);
                boolean isPlaying = getIsPlaying(username, game);
                String message = "";
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                currConnections.broadcastSelf(username, serverMessage);
                currGame = game;
                if(isPlaying) {
                    message = String.format("User '%s' has joined the game playing as %s.", username, playerColor);
                } else {
                    message = String.format("User '%s' is now observing the game.", username);
                }
                serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                currConnections.broadcast(username, serverMessage);
            }

        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.get(gameID).broadcastSelf(username, serverMessage);
        }
    }

    //took a game object
    private void makeMove(String username, int gameID, ChessMove move, Session session) throws IOException {
        try {
            String message = "";
            GameData game = dataAccess.getGame(gameID);
            ChessPiece requestedPiece = game.game().getBoard().getPiece(move.getStartPosition());
            String pieceString = requestedPiece.getPieceType().toString();
            ConnectionManager currConnections = connections.get(gameID);
            if(game.isOver()) {
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "the game is over; no more moves may be made.");
                session.getRemote().sendString(serverMessage.toString());
            } else if (!getPlayerColor(username, game).equalsIgnoreCase(requestedPiece.getTeamColor().toString())) {
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "that is not your piece.");
                session.getRemote().sendString(serverMessage.toString());
            } else if(!getPlayerColor(username, game).equalsIgnoreCase(game.game().getTeamTurn().toString())) {
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "it is not your turn.");
                session.getRemote().sendString(serverMessage.toString());
            } else {
                try {
                    game.game().makeMove(move);
                    ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                    currConnections.broadcast(null, serverMessage);
                    dataAccess.updateGame(gameID, game);
                    String moveString = String.format("a %s from %s to %s", pieceString,
                            getBoardPosition(move.getStartPosition()), getBoardPosition(move.getEndPosition()));
                    message = String.format("Player '%s' moved %s.", username, moveString);
                    //String statusMessage = getGameStatusMessage(game);
                    message += " " + getGameStatusMessage(game);
                    // HEY, YOU: there's probably a better way to do this; server sends game state if changed
                    if (message.contains("white is in checkmate") || message.contains("white is in stalemate")) {
                        dataAccess.endGame(game);
                        message += "Team black has won. Thank you for playing!";
                    } else if(message.contains("black is in checkmate") || message.contains("black is in stalemate")) {
                        dataAccess.endGame(game);
                        message += "Team white has won. Thank you for playing!";
                    }
                    serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    currConnections.broadcast(username, serverMessage);
                } catch (InvalidMoveException ex) {
                    ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                            "invalid move.");
                    session.getRemote().sendString(serverMessage.toString());
                } catch (Exception ex) {
                    ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                            ex.getMessage());
                    currConnections.broadcastSelf(username, serverMessage);
                }
            }
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.get(gameID).broadcastSelf(username, serverMessage);
        }
    }

    // this may not always work? check back in
    private void leave(String username, int gameID) throws IOException{
        try {
            GameData game = dataAccess.getGame(gameID);
            String playerColor = getPlayerColor(username, game);
            boolean isPlaying = getIsPlaying(username, game);
            ConnectionManager currConnections = connections.get(gameID);
            String message = "";
            if(isPlaying) {
                GameData newGame;
                if(playerColor.equalsIgnoreCase("white")) {
                    newGame = new GameData(currGame.gameID(), null, currGame.blackUsername(),
                            currGame.gameName(), game.game(), currGame.isOver());
                } else {
                    //might need to make sure bad things don't happen here
                    newGame = new GameData(currGame.gameID(), currGame.whiteUsername(), null,
                            currGame.gameName(), game.game(), currGame.isOver());
                }
                dataAccess.updateGame(currGame.gameID(), newGame);
                message = String.format("Player '%s' (%s) has left the game.", username, playerColor);
            } else {
                // if not playing, then no change must be made to the game
                message = String.format("Observer '%s' has left the game.", username);
            }
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            currConnections.broadcast(username, serverMessage);
            currConnections.remove(username);
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.get(gameID).broadcastSelf(username, serverMessage);
        }
    }

    private void resign(String username, Session session, int gameID) throws IOException {
        try {
            currGame = dataAccess.getGame(gameID);
            if(currGame.isOver()) {
                var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "the game is already over.");
                session.getRemote().sendString(serverMessage.toString());
            } else if(!username.equalsIgnoreCase(currGame.whiteUsername()) &&
                    !username.equalsIgnoreCase(currGame.blackUsername())) {
                var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "observers may not resign.");
                        session.getRemote().sendString(serverMessage.toString());
            } else {
                dataAccess.endGame(currGame);
                dataAccess.updateGame(currGame.gameID(), currGame);
                String message =
                        String.format("Player '%s' has resigned from the game. Thank you for playing!", username);
                var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.get(gameID).broadcast(null, serverMessage);
            }
        } catch (Exception ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.get(gameID).broadcastSelf(username, serverMessage);
        }
    }

    private String getGameStatusMessage(GameData game) {
        String message = "";
        String username;
        for(var color : ChessGame.TeamColor.values()) {
            if(color.toString().equalsIgnoreCase("white")) {
                username = game.whiteUsername();
            } else {
                username = game.blackUsername();
            }
            if(game.game().isInCheck(color)) {
                message = String.format("Player '%s' (%s) is in check. ", username, color.toString().toLowerCase());
            } else if(game.game().isInCheckmate(color)) {
                message = String.format("Player '%s' (%s) is in checkmate. ", username, color.toString().toLowerCase());
            } else if(game.game().isInStalemate(color)) {
                message = String.format("Player '%s' (%s) is in stalemate. ", username, color.toString().toLowerCase());
            }
        }
        return message;
    }

    private String getPlayerColor(String username, GameData game) {
        if(Objects.equals(username, game.whiteUsername())) {
            return "white";
        } else if(Objects.equals(username, game.blackUsername())) {
            return "black";
        } else { return ""; }
    }

    private boolean getIsPlaying(String username, GameData game) {
        if(Objects.equals(username, game.whiteUsername()) || Objects.equals(username, game.blackUsername())) {
            return true;
        }
        return false;
    }

    private String getBoardPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        String colString = "";
        switch(col) {
            case 1 -> colString = "a";
            case 2 -> colString = "b";
            case 3 -> colString = "c";
            case 4 -> colString = "d";
            case 5 -> colString = "e";
            case 6 -> colString = "f";
            case 7 -> colString = "g";
            case 8 -> colString = "h";
        }
        return colString + row;
    }

    private boolean connectionManagerExists(int key) {
        // for my little brain: if there's a value associated with the key (!null), return true
        return connections.get(key) != null;
    }

}
