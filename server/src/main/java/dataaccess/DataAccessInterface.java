package dataaccess;

import model.*;

import java.util.ArrayList;

public interface DataAccessInterface {

    // These may be changed as necessary moving forward; for now, I'm adding them as a reminder for myself

    public UserData getUser(String username);
    public void addUser(UserData userData);

    public AuthData getAuth(String authToken) throws DataAccessException;
    public void addAuth(AuthData authData);
    public void delAuth(String authToken) throws DataAccessException;

    public GameData getGame(int gameID);
    public void addGame(GameData gameData);
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException;
    public ArrayList<GameData> getGames();

    public void clearData() throws DataAccessException;

}
