package records;

//import model.GameData;
import websocket.commands.UserGameCommand;

//boolean isPlaying, String playerColor, GameData game
public record UserGameCommandRecord(String givenUser, UserGameCommand userGameCommand) {
}
