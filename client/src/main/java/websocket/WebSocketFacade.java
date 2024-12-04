package websocket;

import com.google.gson.Gson;
import exceptionhandling.ResponseException;
import model.AuthData;
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

    public void joinGame(AuthData authData, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), gameID);
            UserGameCommandRecord userGameCommRec = new UserGameCommandRecord(authData.username(), command);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommRec));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(AuthData authData, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.OBSERVE, authData.authToken(), gameID);
            UserGameCommandRecord userGameCommRec = new UserGameCommandRecord(authData.username(), command);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommRec));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(AuthData authData, int gameID) throws ResponseException {

    }

    public void leaveGame(AuthData authData, int gameID) throws ResponseException {
        try {
            //fixme this is just basic garbage
            var message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "you suck");
            this.session.getBasicRemote().sendText(new Gson().toJson(message));
            this.session.close();
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resignFromGame(AuthData authData, int gameID) throws ResponseException {

    }

}
