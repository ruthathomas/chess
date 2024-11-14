package client;

import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.requests.LoginRequest;
import ui.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    static UserData existingUser = new UserData("exists", "exists", "exists@email.com");

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
        var auth = facade.login(new LoginRequest(existingUser.username(), existingUser.password()));
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
        var auth = facade.login(new LoginRequest(existingUser.username(), existingUser.password()));
        assertDoesNotThrow(() -> {facade.logout(auth.authToken());});
    }

    @Test
    void logoutInvalidUser() throws ResponseException {
        var auth = facade.login(new LoginRequest(existingUser.username(), existingUser.password()));
        facade.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> {facade.logout(auth.authToken());});
        assertThrows(ResponseException.class, () -> {facade.logout(null);});
        assertThrows(ResponseException.class, () -> {facade.logout("badToken");});
    }

    // listGames createGame joinGame

}
