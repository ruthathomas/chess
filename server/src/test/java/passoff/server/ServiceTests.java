package passoff.server;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private AuthService authService = new AuthService();
    private GameService gameService = new GameService();
    private UserService userService = new UserService();

    // Sample values for pre-existing data in the memory (database has not yet been set up)
    private AuthData existingAuth = new AuthData("testToken", "existingUser");
    private GameData existingGame = new GameData(1234, "existingUser", "me", "existingGame", new ChessGame());
    private UserData existingUser = new UserData("existingUser", "existingPassword", "existing@email.com");

//    @BeforeAll
//    public static void init() {
//
//    }

    @Test
    public void getExistingAuth() {
        authService.addAuth(existingAuth);
        AuthData testAuth = authService.getAuth("testToken");
        assertNotNull(testAuth);
        assertEquals(testAuth.username(), "existingUser");
    }

    @Test
    public void getNonexistentAuth() {
        //FIXME
    }

    @Test
    public void clear() {

        //FIXME add a part to clear for game and user data

    }

}
