package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import server.ResponseException;

public class AuthService {

    private DataAccessInterface dataAccess;

    public AuthService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Creates a new AuthData object for the indicated user and passes it to the DAO
    public AuthData createAuth(String username) throws ResponseException {
        try {
            if (username == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            String authToken = generateToken();
            AuthData newAuth = new AuthData(authToken, username);
            dataAccess.addAuth(newAuth);
            return newAuth;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // Only used for testing purposes; adds an auth object directly to memory.
    public AuthData addAuth(AuthData authData) throws ResponseException {
        try {
            dataAccess.addAuth(authData);
            return authData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        try {
            return dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void delAuth(String authToken) throws ResponseException {
        try {
            dataAccess.delAuth(authToken);
        } catch (dataaccess.DataAccessException e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearAuthData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
