package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class SQLDataAccess implements DataAccessInterface {

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        //
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void addAuth(AuthData authData) {
        //
    }

    @Override
    public void delAuth(String authToken) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void addGame(GameData gameData) {
        //
    }

    @Override
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void clearData() throws DataAccessException {
        //
    }

}
