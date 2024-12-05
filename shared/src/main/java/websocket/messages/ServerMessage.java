package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    //delete me later
    private String message = "";
    private GameData game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    //delete me
    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        if(message != null) {
            this.message = message;
        }
    }

    public ServerMessage(ServerMessageType type, GameData game, String gameString) {
        this.serverMessageType = type;
        this.game = game;
        this.message = gameString;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    //delete me
    public String getMessage() {
        return message;
    }

    public GameData fetchGame() {
        return game;
    }

}
