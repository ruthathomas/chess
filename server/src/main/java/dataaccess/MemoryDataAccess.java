package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class MemoryDataAccess implements DataAccessInterface {

    private ArrayList<AuthData> authDataArrayList = new ArrayList<>();
    private ArrayList<GameData> gameDataArrayList = new ArrayList<>();
    private ArrayList<UserData> userDataArrayList = new ArrayList<>();

    // First runthrough: not caring about exceptions
    // OKAY THIS COULD BE BETTER BUT I'M GONNA COME BACK TO THAT

    @Override
    public UserData getUser(String username) {
        for(var user : userDataArrayList) {
            if(user.username() == username) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        userDataArrayList.add(userData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for(var auth : authDataArrayList) {
            if(auth.authToken() == authToken) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void addAuth(AuthData authData) {
        authDataArrayList.add(authData);
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        boolean tokenExists = false;
        for(var auth : authDataArrayList) {
            if(auth.authToken() == authToken) {
                tokenExists = true;
                authDataArrayList.remove(auth);
                break;
            }
        }
        if(!tokenExists) {
            throw new DataAccessException("AuthToken did not exist in list");
        }
    }

    @Override
    public GameData getGame(int gameID) {
        for(var game : gameDataArrayList) {
            if(game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void addGame(GameData gameData) {
        gameDataArrayList.add(gameData);
    }

    @Override
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException {
        //FIXME this is probably not the best way to do this
        GameData gameToUpdate = getGame(gameID);
        if(gameToUpdate == null) {
            throw new DataAccessException("TEST ERROR");
        }
        gameDataArrayList.remove(gameToUpdate);
        gameDataArrayList.add(gameData);
        //FIXME why are we returning here??
        return gameData;
    }

    @Override
    public ArrayList<GameData> getGames() {
        return gameDataArrayList;
    }

    @Override
    public void clearData() throws DataAccessException {
        // clear() should not fail; however, if an issue is encountered, an error will be thrown
        try {
            authDataArrayList.clear();
            gameDataArrayList.clear();
            userDataArrayList.clear();
        } catch (Exception e) {
            throw new DataAccessException("Test; data clearing failed.");
        }
    }

}
