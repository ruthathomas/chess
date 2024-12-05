package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static MemoryDataAccess localMemory = new MemoryDataAccess();
    private static SQLDataAccess databaseMemory;

    // Define AuthData values to be used in testing
    private AuthData existingAuth = new AuthData("existingAuth", "existingUser");
    private AuthData validAuth = new AuthData("validAuth", "validName");
    private AuthData nullTokenAuth = new AuthData(null, "username");
    private AuthData nullUsernameAuth = new AuthData("authToken", null);

    // Define GameData values to be used in testing
    private GameData existingGame = new GameData(12, "existingUser", "", "",
            new ChessGame(), false);
    private GameData validGame = new GameData(13, "", "", "validGame",
            new ChessGame(), false);
    private GameData invalidNameGame = new GameData(16, "", "", null,
            new ChessGame(), false);
    private GameData invalidGameGame = new GameData(117, "", "", "",
            null, false);
    private GameData updateGame = new GameData(12, "existingUser", "newPlayer",
            "gameName", new ChessGame(), false);
    private HashMap<Integer, GameData> gamesList = new HashMap<>();

    // Define UserData values to be used in testing
    private UserData existingUser = new UserData("existingUser", "exists", "exists@email.com");
    private UserData validUser = new UserData("validName", "validPassword", "valid@email.com");
    private UserData invalidUsernameUser = new UserData(null, "password", "email@email.com");
    private UserData invalidPasswordUser = new UserData("username", null, "email@email.com");
    private UserData invalidEmailUser = new UserData("username", "password", null);

    @BeforeEach
    void init() throws DataAccessException {

        databaseMemory = new SQLDataAccess();

        localMemory.clearAuthData();
        localMemory.clearGameData();
        localMemory.clearUserData();
        databaseMemory.clearAuthData();
        databaseMemory.clearGameData();
        databaseMemory.clearUserData();

        localMemory.addAuth(existingAuth);
        databaseMemory.addAuth(existingAuth);
        localMemory.addUser(existingUser);
        databaseMemory.addUser(existingUser);
        localMemory.addGame(existingGame);
        databaseMemory.addGame(existingGame);

        gamesList.put(existingGame.gameID(), existingGame);
    }

    @AfterAll
    static void finish() throws DataAccessException {
        localMemory.clearAuthData();
        localMemory.clearGameData();
        localMemory.clearUserData();
        databaseMemory.clearAuthData();
        databaseMemory.clearGameData();
        databaseMemory.clearUserData();
    }

    // Test AuthData requests

    @Test
    void getExistingAuthLocal() {
        assertDoesNotThrow(()->{localMemory.getAuth("existingAuth");});
        assertEquals(existingAuth, localMemory.getAuth("existingAuth"));
    }

    @Test
    void getExistingAuthDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.getAuth("existingAuth");});
        assertEquals(existingAuth, databaseMemory.getAuth("existingAuth"));
    }

    @Test
    void getNonexistentAuthLocal() {
        assertNull(localMemory.getAuth("fakeToken"));
    }

    @Test
    void getNonexistentAuthDatabase() throws DataAccessException {
        assertNull(databaseMemory.getAuth("fakeToken"));
    }

    @Test
    void addValidAuthLocal() {
        assertDoesNotThrow(()->{localMemory.addAuth(validAuth);});
        assertEquals(validAuth, localMemory.getAuth(validAuth.authToken()));
    }

    @Test
    void addValidAuthDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.addAuth(validAuth);});
        assertEquals(validAuth, databaseMemory.getAuth(validAuth.authToken()));
    }

    @Test
    void addInvalidAuthLocal() {
        assertThrows(DataAccessException.class, ()->{localMemory.addAuth(nullTokenAuth);});
        assertThrows(DataAccessException.class, ()->{localMemory.addAuth(nullUsernameAuth);});
    }

    @Test
    void addInvalidAuthDatabase() {
        assertThrows(DataAccessException.class, ()->{databaseMemory.addAuth(nullTokenAuth);});
        assertThrows(DataAccessException.class, ()->{databaseMemory.addAuth(nullUsernameAuth);});
    }

    @Test
    void delExistingAuthLocal() {
        assertDoesNotThrow(()->localMemory.delAuth(existingAuth.authToken()));
        assertNull(localMemory.getAuth(existingAuth.authToken()));
    }

    @Test
    void delExistingAuthDatabase() throws DataAccessException {
        assertDoesNotThrow(()->databaseMemory.delAuth(existingAuth.authToken()));
        assertNull(databaseMemory.getAuth(existingAuth.authToken()));
    }

    @Test
    void delNonexistentAuthLocal() throws DataAccessException {
        localMemory.delAuth(existingAuth.authToken());
        assertDoesNotThrow(()->{localMemory.delAuth(existingAuth.authToken());});
    }

    @Test
    void delNonexistentAuthDatabase() throws DataAccessException {
        databaseMemory.delAuth(existingAuth.authToken());
        assertDoesNotThrow(()->{databaseMemory.delAuth(existingAuth.authToken());});
    }

    @Test
    void clearAuthLocal() {
        assertDoesNotThrow(()->{localMemory.clearAuthData();});
    }

    @Test
    void clearAuthDatabase() {
        assertDoesNotThrow(()->{databaseMemory.clearAuthData();});
    }

    @Test
    void clearEmptyAuthLocal() {
        localMemory.clearAuthData();
        assertDoesNotThrow(()->{localMemory.clearAuthData();});
    }

    @Test
    void clearEmptyAuthDatabase() throws DataAccessException {
        databaseMemory.clearAuthData();
        assertDoesNotThrow(()->{databaseMemory.clearAuthData();});
    }

    // Test GameData requests

    @Test
    void getExistingGameLocal() {
        assertDoesNotThrow(()->{localMemory.getGame(12);});
        assertEquals(existingGame, localMemory.getGame(12));
    }

    @Test
    void getExistingGameDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.getGame(12);});
        assertEquals(existingGame, databaseMemory.getGame(12));
    }

    @Test
    void getNonexistentGameLocal() {
        assertNull(localMemory.getGame(0));
        assertNull(localMemory.getGame(70));
    }

    @Test
    void getNonexistentGameDatabase() throws DataAccessException {
        assertNull(databaseMemory.getGame(0));
        assertNull(databaseMemory.getGame(70));
    }

    @Test
    void getGamesAvailableLocal() throws DataAccessException {
        assertNotNull(localMemory.getGames());
        assertEquals(gamesList, localMemory.getGames());
        localMemory.addGame(validGame);
        gamesList.put(validGame.gameID(), validGame);
        assertEquals(gamesList, localMemory.getGames());
    }

    @Test
    void getGamesAvailableDatabase() throws DataAccessException {
        assertNotNull(databaseMemory.getGames());
        assertEquals(gamesList, databaseMemory.getGames());
        gamesList.put(validGame.gameID(), validGame);
        databaseMemory.addGame(validGame);
        assertEquals(gamesList, databaseMemory.getGames());
    }

    @Test
    void getGamesEmptyLocal() {
        localMemory.clearGameData();
        assertDoesNotThrow(()->{localMemory.getGames();});
        assertEquals(new HashMap<>(), localMemory.getGames());
    }

    @Test
    void getGamesEmptyDatabase() throws DataAccessException {
        databaseMemory.clearGameData();
        assertDoesNotThrow(()->{databaseMemory.getGames();});
        assertEquals(new HashMap<>(), databaseMemory.getGames());
    }

    @Test
    void addValidGameLocal() {
        assertDoesNotThrow(()->{localMemory.addGame(validGame);});
        assertEquals(validGame, localMemory.getGame(validGame.gameID()));
    }

    @Test
    void addValidGameDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.addGame(validGame);});
        assertEquals(validGame, databaseMemory.getGame(validGame.gameID()));
    }

    @Test
    void addInvalidGameLocal() {
        assertThrows(DataAccessException.class, ()->{localMemory.addGame(invalidNameGame);});
        assertThrows(DataAccessException.class, ()->{localMemory.addGame(invalidGameGame);});
    }

    @Test
    void addInvalidGameDatabase() {
        assertThrows(DataAccessException.class, ()->{databaseMemory.addGame(invalidNameGame);});
        assertThrows(DataAccessException.class, ()->{databaseMemory.addGame(invalidGameGame);});
    }

    @Test
    void updateGameValidLocal() {
        assertDoesNotThrow(()->{localMemory.updateGame(existingGame.gameID(), updateGame);});
        assertEquals(updateGame, localMemory.getGame(existingGame.gameID()));
    }

    @Test
    void updateGameValidDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.updateGame(existingGame.gameID(), updateGame);});
        assertEquals(updateGame, databaseMemory.getGame(existingGame.gameID()));
    }

    @Test
    void updateGameInvalidLocal() {
        //his one throws exceptions, but I might change it so that it doesn't, because SQL doesn't
        assertThrows(DataAccessException.class, ()->{localMemory.updateGame(0, updateGame);});
    }

    @Test
    void updateGameInvalidDatabase() {
        // this kind of request doesn't cause any issues with the database
        assertDoesNotThrow(()->{databaseMemory.updateGame(0, updateGame);});
    }

    @Test
    void clearGameLocal() {
        assertDoesNotThrow(()->{localMemory.clearGameData();});
    }

    @Test
    void clearGameDatabase() {
        assertDoesNotThrow(()->{databaseMemory.clearGameData();});
    }

    @Test
    void clearGameEmptyLocal() {
        localMemory.clearGameData();
        assertDoesNotThrow(()->{localMemory.clearGameData();});
    }

    @Test
    void clearGameEmptyDatabase() throws DataAccessException {
        databaseMemory.clearGameData();
        assertDoesNotThrow(()->{databaseMemory.clearGameData();});
    }

    // Test UserData requests

    @Test
    void getExistingUserLocal() {
        assertDoesNotThrow(()->{localMemory.getUser("existingUser");});
        assertEquals(existingUser, localMemory.getUser("existingUser"));
    }

    @Test
    void getExistingUserDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.getUser("existingUser");});
        assertEquals(existingUser, databaseMemory.getUser("existingUser"));
    }

    @Test
    void getNonexistentUserLocal() {
        assertNull(localMemory.getUser("fakeUser"));
    }

    @Test
    void getNonexistentUserDatabase() throws DataAccessException {
        assertNull(databaseMemory.getUser("fakeUser"));
    }

    @Test
    void addValidUserLocal() {
        assertDoesNotThrow(()->{localMemory.addUser(validUser);});
        assertEquals(validUser, localMemory.getUser(validUser.username()));
    }

    @Test
    void addValidUserDatabase() throws DataAccessException {
        assertDoesNotThrow(()->{databaseMemory.addUser(validUser);});
        assertEquals(validUser, databaseMemory.getUser(validUser.username()));
    }

    @Test
    void addInvalidUserLocal() {
        assertThrows(DataAccessException.class, ()->{localMemory.addUser(invalidUsernameUser);});
        assertThrows(DataAccessException.class, ()->{localMemory.addUser(invalidPasswordUser);});
        assertThrows(DataAccessException.class, ()->{localMemory.addUser(invalidEmailUser);});
    }

    @Test
    void addInvalidUserDatabase() throws DataAccessException {
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidEmailUser);});
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidUsernameUser);});
        assertNull(databaseMemory.getUser(invalidUsernameUser.username()));
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidPasswordUser);});
    }

    @Test
    void clearUserLocal() {
        assertDoesNotThrow(()->{localMemory.clearUserData();});
    }

    @Test
    void clearUserDatabase() {
        assertDoesNotThrow(()->{databaseMemory.clearUserData();});
    }

    @Test
    void clearUserEmptyLocal() {
        localMemory.clearUserData();
        assertDoesNotThrow(()->{localMemory.clearUserData();});
    }

    @Test
    void clearUserEmptyDatabase() throws DataAccessException {
        databaseMemory.clearUserData();
        assertDoesNotThrow(()->{databaseMemory.clearUserData();});
    }
}
