package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import model.*;

import java.util.UUID;

public class UserService {

    private MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

     public AuthData register(UserData newUser) {
        if(memoryDataAccess.getUser(newUser.username()) == null) {
            memoryDataAccess.addUser(newUser);
            AuthData newAuth = new AuthData(generateToken(), newUser.username());
            memoryDataAccess.addAuth(newAuth);
            return newAuth;
        } else {
            //fixme give a bad request error
        }
        return null;
    }

    public AuthData login(String username, String password) {
        if(memoryDataAccess.getUser(username) != null) {
            if(matchPassword(username, password)){
                AuthData newAuth = new AuthData(generateToken(), username);
                memoryDataAccess.addAuth(newAuth);
                return newAuth;
            }
            //catch error from match password; fixme
            // basically if match password is false, throw something
        } else {
            //return error bc the user doesn't exist
        }

        return null;
    }

    public void logout(AuthData authData) {
        try {
            memoryDataAccess.getAuth(authData.authToken());
            memoryDataAccess.delAuth(authData.authToken());
        } catch (DataAccessException e) {
            //FIXME do something here
            throw new RuntimeException(e);
        }
        
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * This function is only called if it has been verified that getUser returns non-null;
     * this is why there is no verification within this function.
     * @param username input username
     * @param password input password
     * @return a boolean indicating whether the input password matches that of the input username
     */
    private boolean matchPassword(String username, String password) {
        UserData currentUser = memoryDataAccess.getUser(username);
        if(currentUser.password() == password) {
            return true;
        } else {
            //FIXME have an unauthorized error
        }
        return false;
    }

}
