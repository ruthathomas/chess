package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccessInterface {

    private Map<String, AuthData> authDataMap = new HashMap<>();
    private Map<Integer, GameData> gameDataMap = new HashMap<>();
    private Map<String, UserData> userDataMap = new HashMap<>();
    //FIXME I'd like to change these to maps
    private ArrayList<GameData> gameDataArrayList = new ArrayList<>();

    // First runthrough: not caring about exceptions
    // OKAY THIS COULD BE BETTER BUT I'M GONNA COME BACK TO THAT

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public void addUser(UserData userData) {
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    public void addAuth(AuthData authData) {
        authDataMap.put(authData.authToken(), authData);
    }

    @Override
    //FIXME I think I'm going to change this so it doesn't throw an exception
    public void delAuth(String authToken) throws DataAccessException {
        if(authDataMap.containsKey(authToken)) {
            authDataMap.remove(authToken);
        } else {
            throw new DataAccessException("AuthToken did not exist in list");
        }
    }

    @Override
    public GameData getGame(int gameID) {
        return gameDataMap.get(gameID);
    }

    @Override
    public void addGame(GameData gameData) {
        //fixme this might overwrite data
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException {
        if(gameDataMap.get(gameID) == null) {
            throw new DataAccessException("TEST ERROR");
        }
        gameDataMap.put(gameID, gameData);
        return gameData;
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
