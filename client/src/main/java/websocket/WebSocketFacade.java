package websocket;

import com.google.gson.Gson;
import exceptionhandling.ResponseException;
import model.AuthData;
import model.GameData;
import records.UserGameCommandRecord;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;
    private String websocketUrl = "http://localhost:";

    public WebSocketFacade(int port, NotificationHandler notificationHandler) throws ResponseException {
        try {
            websocketUrl = websocketUrl.replace("http", "ws");
            websocketUrl += port;
            URI socketURI = new URI(websocketUrl + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });

        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(AuthData authData, GameData game, String playerColor) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), game.gameID());
            UserGameCommandRecord userGameCommRec = new UserGameCommandRecord(authData.username(), command, true, playerColor, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommRec));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(AuthData authData, GameData game) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), game.gameID());
            UserGameCommandRecord userGameCommRec = new UserGameCommandRecord(authData.username(), command, false, null, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommRec));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(AuthData authData, int gameID) throws ResponseException {

    }

    public void leaveGame(AuthData authData, int gameID, boolean isPlaying, String playerColor) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authData.authToken(), gameID);
            UserGameCommandRecord userGameCommRec = new UserGameCommandRecord(authData.username(), command, isPlaying, playerColor, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommRec));
            this.session.close();
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resignFromGame(AuthData authData, int gameID) throws ResponseException {

    }

}
