package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.requests.LoginRequest;
import ui.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    static UserData existingUser = new UserData("exists", "exists", "exists@email.com");
    static LoginRequest existingLoginReq = new LoginRequest(existingUser.username(), existingUser.password());

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);

        // clear database for testing and add existing data
        facade.clear();
        AuthData auth = facade.register(existingUser);
        facade.logout(auth.authToken());
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        facade.clear();
        server.stop();
    }

    @Test
    void registerValidUser() throws ResponseException {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
        facade.logout(authData.authToken());
    }

    @Test
    void registerBadUser() {
        assertThrows(ResponseException.class, () -> {facade.register(
                new UserData("badPlayer", null, "bad@email.com"));});
    }

    @Test
    void loginValidUser() throws ResponseException {
        var auth = facade.login(existingLoginReq);
        assertTrue(auth.authToken().length() > 10);
        facade.logout(auth.authToken());
    }

    @Test
    void loginInvalidUser() {
        assertThrows(ResponseException.class, () -> {facade.login(new LoginRequest("badUser", "password"));});
        assertThrows(ResponseException.class, () -> {facade.login(new LoginRequest("badUser", null));});
    }

    @Test
    void logoutCurrentUser() throws ResponseException {
        var auth = facade.login(existingLoginReq);
        assertDoesNotThrow(() -> {facade.logout(auth.authToken());});
    }

    @Test
    void logoutInvalidUser() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        facade.logout(auth);
        assertThrows(ResponseException.class, () -> {facade.logout(auth);});
        assertThrows(ResponseException.class, () -> {facade.logout(null);});
        assertThrows(ResponseException.class, () -> {facade.logout("badToken");});
    }

    @Test
    void listGamesEmpty() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        assertDoesNotThrow(() -> {facade.listGames(auth);});
        facade.logout(auth);
    }

    @Test
    void listGamesNonEmpty() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        facade.createGame(auth, "Game1");
        facade.createGame(auth, "Game2");
        facade.createGame(auth, "Game3");
        Map<Integer, GameData> expectedMap = new HashMap<>();
        expectedMap.put(1, new GameData(1, null, null, "Game1", new ChessGame()));
        expectedMap.put(2, new GameData(2, null, null, "Game2", new ChessGame()));
        expectedMap.put(3, new GameData(3, null, null, "Game3", new ChessGame()));
        assertEquals(expectedMap, facade.listGames(auth));
        //FIXME this is a work in progress
    }

    // listGames createGame joinGame

}
