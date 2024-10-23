package dataaccess;

import model.*;

import java.util.Map;

public interface DataAccessInterface {

    // These may be changed as necessary moving forward; for now, I'm adding them as a reminder for myself

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
