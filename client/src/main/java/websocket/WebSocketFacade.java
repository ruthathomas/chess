package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exceptionhandling.ResponseException;
import model.AuthData;
import websocket.commands.*;
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
                    //var
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

    public void connectToGame(AuthData authData, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(AuthData authData, int gameID, ChessMove move) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authData.authToken(), gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(AuthData authData, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authData.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            //failure happening in handling due to null pointer exception; then, the session is still closed, which is causing you some errors
            this.session.close();
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resignFromGame(AuthData authData, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authData.authToken(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }

    }

}
