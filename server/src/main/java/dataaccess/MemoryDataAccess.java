package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class MemoryDataAccess implements DataAccessInterface {

    private ArrayList<UserData> userDataArrayList = new ArrayList<>();

    @Override
    public UserData getUser(String username) {
        // if there's userdata with that username, return that
        for(var dataItem : userDataArrayList) {
            if(dataItem.username() == username) {
                return dataItem;
            }
        }
        // else, return null
        return null;
    }

    @Override
    public AuthData createUser(UserData userData) {
        return null;
    }

    @Override
    public AuthData createAuth(AuthData authData) {
        return null;
    }

}
