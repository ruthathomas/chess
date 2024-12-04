package records;

import model.GameData;
import websocket.commands.UserGameCommand;

public record UserGameCommandRecord(String givenUser, UserGameCommand userGameCommand, Boolean isPlaying, String playerColor, GameData game) {
}
