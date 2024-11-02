package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private DataAccessInterface dataAccess;

    public UserService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

     public AuthData register(UserData newUser) throws ResponseException {
         try {
             if(dataAccess.getUser(newUser.username()) == null) {
                 if (newUser.username() == null || newUser.password() == null || newUser.email() == null ||
                         newUser.username() == "" || newUser.password() == "" || newUser.email() == "") {
                     throw new ResponseException(400, "Error: bad request");
                 }
                 var hashedPassword = BCrypt.hashpw(newUser.password(), BCrypt.gensalt());
                 newUser = new UserData(newUser.username(), hashedPassword, newUser.email());
                 dataAccess.addUser(newUser);
                 AuthData newAuth = new AuthData(generateToken(), newUser.username());
                 dataAccess.addAuth(newAuth);
                 return newAuth;
             } else {
                 throw new ResponseException(403, "Error: already taken");
             }
         } catch (DataAccessException e) {
             throw new ResponseException(500, e.getMessage());
         }
     }

    public AuthData login(String username, String password) throws ResponseException {
        try {
            if(dataAccess.getUser(username) != null) {
                if(matchPassword(username, password)){
                    AuthData newAuth = new AuthData(generateToken(), username);
                    dataAccess.addAuth(newAuth);
                    return newAuth;
                } else {
                    // if matchPassword returns false, throw an error
                    throw new ResponseException(401, "Error: unauthorized");
                }
            } else {
                // throw an error because the user doesn't exist
                throw new ResponseException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void logout(String authToken) throws ResponseException {
        try {
            if(dataAccess.getAuth(authToken) != null) {
                dataAccess.delAuth(authToken);
            } else {
                // if the token doesn't exist in the data set, access is unauthorized
                throw new ResponseException(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearUserData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // For testing use only; the testing suite calls this to add "existing" data before running tests.
    public void addUser(UserData userData) throws ResponseException {
        try {
            dataAccess.addUser(userData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    //This function is only called if it has been verified that getUser returns non-null;
    //     this is why there is no verification within this function.
    private boolean matchPassword(String username, String password) throws ResponseException {
        UserData currentUser;
        try {
            currentUser = dataAccess.getUser(username);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
        return BCrypt.checkpw(password, currentUser.password());
    }

}
