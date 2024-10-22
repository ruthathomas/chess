package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import server.ResponseException;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

    //FIXME needs some more error stuff; maybe stop throwing errors? idk
     public AuthData register(UserData newUser) throws ResponseException {
        if(memoryDataAccess.getUser(newUser.username()) == null) {
            if (newUser.username() == "" || newUser.password() == "" || newUser.email() == "") {
                throw new ResponseException(400, "Error: bad request");
            }
            memoryDataAccess.addUser(newUser);
            AuthData newAuth = new AuthData(generateToken(), newUser.username());
            memoryDataAccess.addAuth(newAuth);
            return newAuth;
        } else {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    public AuthData login(String username, String password) throws ResponseException{
        if(memoryDataAccess.getUser(username) != null) {
            if(matchPassword(username, password)){
                AuthData newAuth = new AuthData(generateToken(), username);
                memoryDataAccess.addAuth(newAuth);
                return newAuth;
            } else {
                // if matchPassword returns false, throw an error
                throw new ResponseException(401, "Error: unauthorized");
            }
        } else {
            // throw an error because the user doesn't exist
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    //FIXME this doesn't account for 500? idk if it needs to
    public void logout(String authToken) throws ServiceException {
        memoryDataAccess.getAuth(authToken);
        try {
            memoryDataAccess.delAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException("FIXME I'm WORKING ON ITT");
        }
    }

    public void clearData() throws ServiceException {
        memoryDataAccess.clearUserData();
    }

    /**
     * For testing use only; the testing suite calls this to add "existing" data before running tests.
     * @param userData data to add
     */
    public void addUser(UserData userData) {
        memoryDataAccess.addUser(userData);
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
        if(Objects.equals(currentUser.password(), password)) {
            return true;
        } else {
            return false;
        }
    }

}
