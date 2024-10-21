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
    public AuthData getAuth(String authToken) {
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
    public void delAuth(String authToken) {
        for(var auth : authDataArrayList) {
            if(auth.authToken() == authToken) {
                authDataArrayList.remove(auth);
                break;
            }
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
    public GameData updateGame(int gameID, GameData gameData) {
        //FIXME this is probably not the best way to do this
        gameDataArrayList.remove(getGame(gameID));
        gameDataArrayList.add(gameData);
        //FIXME why are we returning here??
        return gameData;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return gameDataArrayList;
    }

    @Override
    public void clearData() {
        authDataArrayList.clear();
        gameDataArrayList.clear();
        userDataArrayList.clear();
    }

}
