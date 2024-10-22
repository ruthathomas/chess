package passoff.server;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public void createGameValid() {
        assertDoesNotThrow(()-> {gameService.createGame("gameName", existingAuth.authToken());});
    }

    //FIXME I feel like I should include at least one more for createGame

    @Test
    public void listGamesTest() {
        // there are games to be listed
        //FIXME you need to change this so that your available games and your resulting games match??
        //assertEquals(availableGames, gameService.listGames(existingAuth.authToken()));
        System.out.println(gameService.listGames(existingAuth.authToken()));
        // there are no available games
        //gameService.clearData();
        //availableGames.clear();
        //assertEquals(availableGames, gameService.listGames(existingAuth.authToken()));
    }

    @Test
    public void gameServiceInvalidToken() {
        // for join, create, and list
        assertThrows(ServiceException.class, ()->{gameService.joinGame(availableGameBlack.gameID(), "black", "invalidToken");});
        assertThrows(ServiceException.class, ()->{gameService.createGame("gameName", "invalidToken");});
        assertThrows(ServiceException.class, ()->{gameService.listGames("invalidToken");});
    }



//    @Test
//    public void createNewGame() {
//        assertNotNull(gameService.createGame("testGame", ));
//    }

//    @Test
//    public void listAllGamesEmpty() {
//        assertEquals(new ArrayList<>(), gameService.listGames());
//    }


    // Set of tests for UserService

    @Test
    public void loginValidData() {
        assertNotNull(userService.login("existingUser", "existingPassword"));
    }

    @Test
    public void loginInvalidData() {
        // invalid username
        assertThrows(ServiceException.class, ()->{userService.login("testUser", "testPassword");} );
        // invalid password
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
        assertDoesNotThrow(()->{authService.clearData();});
        //FIXME add a part to clear for game and user data
    }

}
