package passoff.server;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;
import service.*;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private MemoryDataAccess memory = new MemoryDataAccess();
    private AuthService authService = new AuthService(memory);
    private GameService gameService = new GameService(memory);
    private UserService userService = new UserService(memory);

    // Sample values for pre-existing data in the memory (database has not yet been set up)
    private AuthData existingAuth = new AuthData("testToken", "existingUser");
    private GameData fullGame = new GameData(1234, "existingUser", "me", "fullGame", new ChessGame());
    private GameData availableGameWhite = new GameData(4321, "", "testingUser", "availableGame", new ChessGame());
    private GameData availableGameBlack = new GameData(2, "whiteUser", "", "availableGame", new ChessGame());
    private UserData existingUser = new UserData("existingUser", "existingPassword", "existing@email.com");

    @BeforeEach
    public void init() {
        authService.clearData();
        gameService.clearData();
        userService.clearData();
        authService.addAuth(existingAuth);
        gameService.addGame(fullGame);
        gameService.addGame(availableGameBlack);
        gameService.addGame(availableGameWhite);
        userService.addUser(existingUser);

    }

    // Set of tests for AuthService

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

    // Set of tests for GameService

    @Test
    public void joinValidGame() {
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameWhite.gameID(), "white", existingAuth.authToken());});
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameBlack.gameID(), "black", existingAuth.authToken());});
    }

    @Test
    public void joinAvailableGameColorTaken() {
        //FIXME 2: should we prevent someone from being part of multiple games at once?
        assertThrows(ServiceException.class, ()->{gameService.joinGame(availableGameWhite.gameID(), "black", existingAuth.authToken());});
        assertThrows(ServiceException.class, ()->{gameService.joinGame(availableGameBlack.gameID(), "white", existingAuth.authToken());});
    }

    @Test
    public void joinGameColorNotLowercase() {
        //FIXME : in future, I'd like to make the colors into an enum
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameWhite.gameID(), "WHITE", existingAuth.authToken());});
        assertThrows(ServiceException.class, ()-> {gameService.joinGame(availableGameWhite.gameID(), "WhITe", existingAuth.authToken());});
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameBlack.gameID(), "Black", existingAuth.authToken());});
        assertThrows(ServiceException.class, ()-> {gameService.joinGame(availableGameBlack.gameID(), "BLaCk", existingAuth.authToken());});
    }

    @Test
    public void joinInvalidGame() {
        // invalid ID
        assertThrows(ServiceException.class, ()->{gameService.joinGame(0, "white", existingAuth.authToken());});
        // unjoinable game (full)
        assertThrows(ServiceException.class, ()-> {gameService.joinGame(fullGame.gameID(), "white", existingAuth.authToken());});
        assertThrows(ServiceException.class, ()-> {gameService.joinGame(fullGame.gameID(), "black", existingAuth.authToken());});
    }

    @Test
    public void createValidGame() {
        assertDoesNotThrow(()-> {gameService.createGame("gameName", existingAuth.authToken());});
    }

    @Test
    public void listGamesTest() throws ResponseException {
        // there are games to be listed
        Map<Integer, GameData> expectedData = new HashMap<>();
        expectedData.put(fullGame.gameID(), fullGame);
        expectedData.put(availableGameWhite.gameID(), availableGameWhite);
        expectedData.put(availableGameBlack.gameID(), availableGameBlack);
        assertEquals(expectedData, gameService.listGames(existingAuth.authToken()));
        // there are no available games
        gameService.clearData();
        expectedData.clear();
        assertEquals(expectedData, gameService.listGames(existingAuth.authToken()));
    }

    @Test
    public void gameServiceInvalidToken() {
        // for join, create, and list
        assertThrows(ServiceException.class, ()->{gameService.joinGame(availableGameBlack.gameID(), "black", "invalidToken");});
        assertThrows(ServiceException.class, ()->{gameService.createGame("gameName", "invalidToken");});
        assertThrows(ServiceException.class, ()->{gameService.listGames("invalidToken");});
    }

    // Set of tests for UserService

    @Test
    public void loginValidData() {
        assertDoesNotThrow(()->{userService.login("existingUser", "existingPassword");});
    }

    @Test
    public void loginInvalidData() {
        // invalid username
        assertThrows(ResponseException.class, ()->{userService.login("testUser", "testPassword");} );
        // invalid password
        assertThrows(ResponseException.class, ()->{userService.login("existingUser", "testPassword");} );
    }


    @Test
    public void registerValidUser() {
        assertDoesNotThrow(()->{userService.register(new UserData("testUser", "testPassword", "test@email.com"));});
    }

    @Test
    public void registerInvalidUser() {
        String sampleName = "sampleUser";
        String samplePassword = "samplePassword";
        String sampleEmail = "sample@email.com";
        // Register a user that already exists
        assertThrows(ResponseException.class, ()->{userService.register(existingUser);});
        // Register a user without a username
        assertThrows(ResponseException.class, ()->{userService.register(new UserData("", samplePassword, sampleEmail));});
        // Register a user without a password
        assertThrows(ResponseException.class, ()->{userService.register(new UserData(sampleName, "", sampleEmail));});
        // Register a user without an email
        assertThrows(ResponseException.class, ()->{userService.register(new UserData(sampleName, samplePassword, ""));});
    }

    @Test
    public void logoutValid() {
        assertDoesNotThrow(()->{userService.logout(existingAuth.authToken());});
    }

    @Test
    public void logoutInvalid() throws ResponseException {
        assertThrows(ResponseException.class, ()->{userService.logout("badToken");});
        userService.logout(existingAuth.authToken());
        assertThrows(ResponseException.class, ()->{userService.logout(existingAuth.authToken());});
    }

    @Test
    public void clear() {
        assertDoesNotThrow(()->{authService.clearData();});
        assertDoesNotThrow(()->{gameService.clearData();});
        assertDoesNotThrow(()->{userService.clearData();});
    }

}
