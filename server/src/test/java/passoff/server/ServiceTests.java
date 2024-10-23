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

    /*
    Private member functions of the services are not tested here.
    Each public method on your Service classes has two test cases, one positive test and one negative test
        clearData
     */

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
    private Map<Integer, GameData> expectedData = new HashMap<>();

    /**
     * The functions 'addAuth,' 'addGame,' and 'addUser' are ONLY used in testing;
     * they have not been given tests in the testing suite because they are not
     * intended to be used in actual execution of the program.
     */
    @BeforeEach
    public void init() throws ResponseException {
        // clear data
        authService.clearData();
        gameService.clearData();
        userService.clearData();
        // add the variables to memory
        authService.addAuth(existingAuth);
        gameService.addGame(fullGame);
        gameService.addGame(availableGameBlack);
        gameService.addGame(availableGameWhite);
        userService.addUser(existingUser);
        // add test games to expectedData
        expectedData.put(fullGame.gameID(), fullGame);
        expectedData.put(availableGameWhite.gameID(), availableGameWhite);
        expectedData.put(availableGameBlack.gameID(), availableGameBlack);
    }

    // Set of tests for AuthService

    @Test
    public void createGoodAuth() throws ResponseException {
        assertDoesNotThrow(()->{authService.createAuth("testUser");});
        AuthData testAuth = authService.createAuth("testUser");
        assertEquals("testUser", testAuth.username());
    }

    @Test
    public void createBadAuth() {
        // invalid username for auth creation
        assertThrows(ResponseException.class, ()->{authService.createAuth(null);});
    }

    @Test
    public void delExistingAuth() throws ResponseException {
        assertNotNull(authService.getAuth("testToken"));
        assertDoesNotThrow(()-> {authService.delAuth("testToken");});
        assertNull(authService.getAuth("testToken"));
    }

    @Test
    public void delNonexistentAuth() {
        assertNull(authService.getAuth("badToken"));
        assertThrows(ResponseException.class, ()->{authService.delAuth("badToken");});
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
        assertThrows(ResponseException.class, ()->{gameService.joinGame(availableGameWhite.gameID(), "black", existingAuth.authToken());});
        assertThrows(ResponseException.class, ()->{gameService.joinGame(availableGameBlack.gameID(), "white", existingAuth.authToken());});
    }

    @Test
    public void joinGameColorNotLowercase() {
        //FIXME : in future, I'd like to make the colors into an enum
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameWhite.gameID(), "WHITE", existingAuth.authToken());});
        assertThrows(ResponseException.class, ()-> {gameService.joinGame(availableGameWhite.gameID(), "WhITe", existingAuth.authToken());});
        assertDoesNotThrow(()-> {gameService.joinGame(availableGameBlack.gameID(), "Black", existingAuth.authToken());});
        assertThrows(ResponseException.class, ()-> {gameService.joinGame(availableGameBlack.gameID(), "BLaCk", existingAuth.authToken());});
    }

    @Test
    public void joinInvalidGame() {
        // test invalid ID
        assertThrows(ResponseException.class, ()->{gameService.joinGame(0, "white", existingAuth.authToken());});
        // test unjoinable game (full)
        assertThrows(ResponseException.class, ()-> {gameService.joinGame(fullGame.gameID(), "white", existingAuth.authToken());});
        assertThrows(ResponseException.class, ()-> {gameService.joinGame(fullGame.gameID(), "black", existingAuth.authToken());});
    }

    @Test
    public void createGameValidToken() {
        assertDoesNotThrow(()-> {gameService.createGame("gameName", existingAuth.authToken());});
    }

    @Test
    public void createGameInvalidToken() {
        assertThrows(ResponseException.class, ()-> {gameService.createGame("gameName", "badAuth");});
    }

    @Test
    public void listGamesNonEmpty() throws ResponseException {
        assertEquals(expectedData, gameService.listGames(existingAuth.authToken()));
    }

    @Test
    public void listGamesEmpty() throws ResponseException {
        gameService.clearData();
        expectedData.clear();
        // there are no available games
        assertEquals(expectedData, gameService.listGames(existingAuth.authToken()));
    }

    @Test
    public void gameServiceInvalidToken() {
        // for join, create, and list
        assertThrows(ResponseException.class, ()->{gameService.joinGame(availableGameBlack.gameID(), "black", "invalidToken");});
        assertThrows(ResponseException.class, ()->{gameService.createGame("gameName", "invalidToken");});
        assertThrows(ResponseException.class, ()->{gameService.listGames("invalidToken");});
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
        assertThrows(ResponseException.class, ()->{userService.register(new UserData(null, samplePassword, sampleEmail));});
        // Register a user without a password
        assertThrows(ResponseException.class, ()->{userService.register(new UserData(sampleName, null, sampleEmail));});
        // Register a user without an email
        assertThrows(ResponseException.class, ()->{userService.register(new UserData(sampleName, samplePassword, null));});
    }

    @Test
    public void logoutValid() {
        assertDoesNotThrow(()->{userService.logout(existingAuth.authToken());});
    }

    @Test
    public void logoutInvalid() throws ResponseException {
        // invalid because of bad token
        assertThrows(ResponseException.class, ()->{userService.logout("badToken");});
        userService.logout(existingAuth.authToken());
        // previously valid token was removed on logout
        assertThrows(ResponseException.class, ()->{userService.logout(existingAuth.authToken());});
    }

    @Test
    public void clearHasData() {
        assertDoesNotThrow(()->{authService.clearData();});
        assertDoesNotThrow(()->{gameService.clearData();});
        assertDoesNotThrow(()->{userService.clearData();});
    }

    @Test
    public void clearNoData() throws ResponseException {
        authService.clearData();
        gameService.clearData();
        userService.clearData();
        assertDoesNotThrow(()->{authService.clearData();});
        assertDoesNotThrow(()->{gameService.clearData();});
        assertDoesNotThrow(()->{userService.clearData();});
    }

}
