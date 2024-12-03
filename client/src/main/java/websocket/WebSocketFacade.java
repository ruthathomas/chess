package websocket;

import com.google.gson.Gson;
import exceptionhandling.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;
    private String websocketUrl = "ws://localhost:";

    public WebSocketFacade(int port, NotificationHandler notificationHandler) throws ResponseException {
        try {
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

        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    //implement some things here; in petshop, it does enterPetShop and leavePetShop
}
