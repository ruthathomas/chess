package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import exceptionhandling.ResponseException;
import server.Server;
import records.GameListRecord;
import requests.JoinGameRequest;
import requests.LoginRequest;
import ui.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private static UserData existingUser = new UserData("exists", "exists", "exists@email.com");
    private static LoginRequest existingLoginReq = new LoginRequest(existingUser.username(), existingUser.password());
    Collection<GameData> expectedGames = new ArrayList<>();

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
        GameData gameData1 = facade.createGame(auth, "Game1");
        GameData gameData2 = facade.createGame(auth, "Game2");
        GameData gameData3 = facade.createGame(auth, "Game3");
        expectedGames.add(gameData1);
        expectedGames.add(gameData2);
        expectedGames.add(gameData3);
        assertEquals(new GameListRecord(expectedGames), facade.listGames(auth));
        facade.logout(auth);
    }

    @Test
    void createValidGame() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        GameData sampleGame = new GameData(facade.listGames(auth).games().size()+1, null,
                null, "sampleGame", new ChessGame(), false);
        GameData actualGame = facade.createGame(auth, "sampleGame");
        assertEquals(sampleGame, actualGame);
        expectedGames.add(actualGame);
        assertDoesNotThrow(() -> {facade.createGame(auth, "");});
        expectedGames.add(new GameData(facade.listGames(auth).games().size()+1, null,
                null, "", new ChessGame(), false));
        facade.logout(auth);
    }

    @Test
    void createInvalidGame() throws ResponseException {
        assertThrows(ResponseException.class, () -> {facade.createGame("badToken", "newGame");});
        var auth = facade.login(existingLoginReq).authToken();
        assertThrows(ResponseException.class, () -> {facade.createGame(auth, null);});
    }

    @Test
    void joinValidGame() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        GameData validGame = facade.createGame(auth, "validGame");
        int id = validGame.gameID();
        assertDoesNotThrow(() -> {facade.joinGame(auth, new JoinGameRequest("white", id));});
        GameData expectedGame = new GameData(id, existingUser.username(), null, validGame.gameName(),
                validGame.game(), false);
        var games = facade.listGames(auth).games();
        GameData actualGame = null;
        for(var game : games) {
            if(game.gameID() == id) {
                actualGame = game;
            }
        }
        assertEquals(expectedGame, actualGame);
        facade.logout(auth);
    }

    @Test
    void joinInvalidGame() throws ResponseException {
        var auth = facade.login(existingLoginReq).authToken();
        GameData validGame = facade.createGame(auth, "Game");
        int id = validGame.gameID();
        facade.joinGame(auth, new JoinGameRequest("white", id));
        // Request to join as color which is already taken
        assertThrows(ResponseException.class, () -> {facade.joinGame(auth, new JoinGameRequest("white", id));});
    }

}
