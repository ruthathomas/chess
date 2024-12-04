package records;

import websocket.commands.UserGameCommand;

public record UserGameCommandRecord(String givenUser, UserGameCommand userGameCommand, Boolean isPlaying) {
}
