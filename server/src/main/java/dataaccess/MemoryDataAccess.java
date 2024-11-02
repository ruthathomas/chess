package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccessInterface {

    // I've been told (see: Slack) that we need to have all of the DataAccessExceptions, but that
    // they're specifically important for SQL, and we don't necessarily have to have them on the
    // memory side. I don't want to implement them here right now, and I use that as my excuse. Thanks <3

    private Map<String, AuthData> authDataMap = new HashMap<>();
    private Map<Integer, GameData> gameDataMap = new HashMap<>();
    private Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        if(userData.username() == null | userData.password() == null | userData.email() == null) {
            throw new DataAccessException("Insertion failed; cannot insert a null value.");
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        if(authData.authToken() == null | authData.username() == null) {
            throw new DataAccessException("Insertion failed; cannot insert a null value.");
        }
        authDataMap.put(authData.authToken(), authData);
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        authDataMap.remove(authToken);
    }

    @Override
    public GameData getGame(int gameID) {
        return gameDataMap.get(gameID);
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        if(gameData.gameID() == 0 | gameData.game() == null | gameData.whiteUsername() == null |
                gameData.blackUsername() == null | gameData.gameName() == null) {
            throw new DataAccessException("Insertion failed; cannot insert a null value");
        }
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData updateGame(int gameID, GameData newData) throws DataAccessException {
        if(gameDataMap.get(gameID) == null) {
            throw new DataAccessException("Game does not exist in list");
        }
        gameDataMap.put(gameID, newData);
        return newData;
    }

    @Override
    public Map<Integer, GameData> getGames() {
        return gameDataMap;
    }

    @Override
    public void clearAuthData() {
        authDataMap.clear();
    }

    @Override
    public void clearGameData() {
        gameDataMap.clear();
    }

    @Override
    public void clearUserData() {
        userDataMap.clear();
    }

}
