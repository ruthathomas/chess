package dataaccess;

import model.*;

import java.util.Map;

public interface DataAccessInterface {

    // It seems like a lot to me to have all of these throw exceptions, but I've been told it's required.
    // This is a note to self to remind me not to delete them.

    public UserData getUser(String username) throws DataAccessException;
    public void addUser(UserData userData) throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;
    public void addAuth(AuthData authData) throws DataAccessException;
    public void delAuth(String authToken) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;
    public void addGame(GameData gameData) throws DataAccessException;
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException;
    public Map<Integer, GameData> getGames() throws DataAccessException;

    public void clearAuthData() throws DataAccessException;
    public void clearGameData() throws DataAccessException;
    public void clearUserData() throws DataAccessException;

}
