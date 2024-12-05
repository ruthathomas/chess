package records;

//import model.GameData;
import chess.ChessMove;
import model.GameData;
import websocket.commands.UserGameCommand;

//ngl feels pretty franken-object, but I'm at my wit's end currently
public record UserGameCommandRecord(String givenUser, UserGameCommand userGameCommand, boolean isPlaying,
                                    String playerColor, GameData game, String move) {
}
