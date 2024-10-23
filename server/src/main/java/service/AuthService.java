package service;

import java.util.UUID;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import server.ResponseException;

public class AuthService {

    private MemoryDataAccess memoryDataAccess;

    public AuthService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

    // Creates a new AuthData object for the indicated user and passes it to the DAO
    public AuthData createAuth(String username) throws ResponseException {
        if(username == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        String authToken = generateToken();
        AuthData newAuth = new AuthData(authToken, username);
        memoryDataAccess.addAuth(newAuth);
        return newAuth;
    }

    // Only used for testing purposes; adds an auth object directly to memory.
    public AuthData addAuth(AuthData authData){
        memoryDataAccess.addAuth(authData);
        return authData;
    }

    public AuthData getAuth(String authToken) {
        return memoryDataAccess.getAuth(authToken);
    }

    public void delAuth(String authToken) throws ResponseException {
        try {
            memoryDataAccess.delAuth(authToken);
        } catch (dataaccess.DataAccessException e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public void clearData() throws ResponseException {
        memoryDataAccess.clearAuthData();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
