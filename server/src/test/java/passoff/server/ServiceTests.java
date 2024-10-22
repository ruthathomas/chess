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

import java.util.ArrayList;

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
    public void createNewAuth() {
        AuthData testAuth = authService.createAuth("testUser");
        assertNotNull(testAuth.authToken());
        assertEquals("testUser", testAuth.username());
    }

    @Test
    public void delExistingAuth() {
        authService.addAuth(existingAuth);
        assertNotNull(authService.getAuth("testToken"));
        authService.delAuth("testToken");
        assertNull(authService.getAuth("testToken"));
    }

    @Test
    public void getExistingAuth() {
        authService.addAuth(existingAuth);
        AuthData testAuth = authService.getAuth("testToken");
        assertNotNull(testAuth);
        assertEquals("existingUser", testAuth.username());
    }

    @Test
    public void getNonexistentAuth() {
        assertNull(authService.getAuth("testToken"));
        authService.addAuth(existingAuth);
        assertNull(authService.getAuth("testToken2"));
    }

    @Test
    public void createNewGame() {
        assertNotNull(gameService.createGame("testGame"));
    }

//    @Test
//    public void listAllGamesEmpty() {
//        assertEquals(new ArrayList<>(), gameService.listGames());
//    }

    @Test
    public void clear() {
        authService.addAuth(existingAuth);
        assertNull(authService.clearData());
        //FIXME add a part to clear for game and user data
    }

}
