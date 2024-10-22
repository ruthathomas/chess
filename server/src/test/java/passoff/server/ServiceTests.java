package passoff.server;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import service.AuthService;
import service.GameService;
import service.ServiceException;
import service.UserService;

import java.lang.reflect.Executable;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private MemoryDataAccess memory = new MemoryDataAccess();
    private AuthService authService = new AuthService(memory);
    private GameService gameService = new GameService(memory);
    private UserService userService = new UserService(memory);

    // Sample values for pre-existing data in the memory (database has not yet been set up)
    private AuthData existingAuth = new AuthData("testToken", "existingUser");
    private GameData existingGame = new GameData(1234, "existingUser", "me", "existingGame", new ChessGame());
    private UserData existingUser = new UserData("existingUser", "existingPassword", "existing@email.com");

    @BeforeEach
    public void init() {
        authService.clearData();
        authService.addAuth(existingAuth);
        userService.addUser(existingUser);
        //FIXME add a thing to add game for testing
    }

    @Test
    public void createNewAuth() {
        AuthData testAuth = authService.createAuth("testUser");
        assertNotNull(testAuth.authToken());
        assertEquals("testUser", testAuth.username());
    }

    @Test
    public void delExistingAuth() {
        assertNotNull(authService.getAuth("testToken"));
        authService.delAuth("testToken");
        assertNull(authService.getAuth("testToken"));
    }

    @Test
    public void getExistingAuth() {
        AuthData testAuth = authService.getAuth("testToken");
        assertNotNull(testAuth);
        assertEquals("existingUser", testAuth.username());
    }

    @Test
    public void getNonexistentAuth() {
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

    //Something here is problematic; it runs on its own, but when the whole thing goes, it doesn't
    @Test
    public void loginValidUsername() {
        assertNotNull(userService.login("existingUser", "existingPassword"));
    }

    @Test
    public void loginInvalidUsername() {
        assertThrows(ServiceException.class, ()->{userService.login("testUser", "testPassword");} );
    }

    @Test
    public void loginInvalidPassword() {
        assertThrows(ServiceException.class, ()->{userService.login("existingUser", "testPassword");} );
    }


    @Test
    public void registerValidUser() {
        assertNotNull(userService.register(new UserData("testUser", "testPassword", "test@email.com")));
    }

    @Test
    public void registerInvalidUser() {
        assertThrows(ServiceException.class, ()->{userService.register(existingUser);});
    }

    @Test
    public void logoutValid() {
        assertDoesNotThrow(()->{userService.logout(existingAuth);});
    }

    @Test
    public void logoutInvalid() {
        assertThrows(ServiceException.class, ()->{userService.logout(new AuthData("newToken", "thisUser"));});
        userService.logout(existingAuth);
        assertThrows(ServiceException.class, ()->{userService.logout(existingAuth);});
    }

    @Test
    public void clear() {
        authService.addAuth(existingAuth);
        assertNull(authService.clearData());
        //FIXME add a part to clear for game and user data
    }

}
