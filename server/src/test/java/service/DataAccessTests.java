package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private MemoryDataAccess localMemory = new MemoryDataAccess();
    private SQLDataAccess databaseMemory = new SQLDataAccess();

    // Define AuthData values to be used in testing
    private AuthData existingAuth = new AuthData("existingAuth", "existingUser");
    private AuthData validAuth = new AuthData("validAuth", "validName");
    private AuthData nullTokenAuth = new AuthData(null, "username");
    private AuthData nullUsernameAuth = new AuthData("authToken", null);

    // Define UserData values to be used in testing
    private UserData existingUser = new UserData("existingUser", "exists", "exists@email.com");
    private UserData validUser = new UserData("validName", "validPassword", "valid@email.com");
    private UserData invalidUsernameUser = new UserData(null, "password", "email@email.com");
    private UserData invalidPasswordUser = new UserData("username", null, "email@email.com");
    private UserData invalidEmailUser = new UserData("username", "password", null);

    @BeforeEach
    void init() throws DataAccessException {
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
    void getNonexistentAuthDatabase() {
        assertThrows(DataAccessException.class, ()->{databaseMemory.getAuth("fakeToken");});
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
    void delExistingAuthDatabase() {
        assertDoesNotThrow(()->databaseMemory.delAuth(existingAuth.authToken()));
        assertThrows(DataAccessException.class, ()->{databaseMemory.getAuth(existingAuth.authToken());});
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
    void getGame() {
    }

    @Test
    void getGames() {
    }

    @Test
    void addGame() {
    }

    @Test
    void updateGame() {
    }

    @Test
    void clearGame() {
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
    void getNonexistentUserDatabase() {
        assertThrows(DataAccessException.class, ()->{databaseMemory.getUser("fakeUser");});
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
    void addInvalidUserDatabase() {
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidUsernameUser);});
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidPasswordUser);});
        assertThrows(DataAccessException.class, ()->{databaseMemory.addUser(invalidEmailUser);});
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
