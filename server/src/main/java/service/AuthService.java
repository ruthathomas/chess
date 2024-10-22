package service;

import java.util.UUID;

import dataaccess.MemoryDataAccess;
import model.AuthData;

public class AuthService {

    private MemoryDataAccess memoryDataAccess;

    public AuthService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

    /**
     * Creates a new AuthData object for the indicated user and passes it to the DAO.
     * @param username username of the user for which to create the AuthData.
     * @return The newly created AuthData object.
     */
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        // If the unlikely occurs and the authToken already exists, generate a new one
        while(getAuth(authToken) != null) {
            authToken = generateToken();
        }
        AuthData newAuth = new AuthData(authToken, username);
        memoryDataAccess.addAuth(newAuth);
        return newAuth;
    }

    /**
     * A function for testing purposes, used to add an authData object with a known authToken.
     * In actual execution of the program, no one will need to know the authToken except for
     * the data layer.
     * @param authData provided AuthData object.
     * @return provided AuthData object.
     */
    public AuthData addAuth(AuthData authData) {
        memoryDataAccess.addAuth(authData);
        return authData;
    }

    // It feels dumb to have this function that just calls another function, but
    public AuthData getAuth(String authToken) {
        return memoryDataAccess.getAuth(authToken);
    }

    public void delAuth(String authToken) throws ServiceException {
        try {
            memoryDataAccess.delAuth(authToken);
        } catch (dataaccess.DataAccessException e) {
            throw new ServiceException("FIXME this is an error bc bad data");
        }
    }

    //FIXME change the try-catch block
    public void clearData() throws ServiceException {
        try {
            memoryDataAccess.clearData();
        } catch (dataaccess.DataAccessException e) {
            throw new ServiceException("FIXME");
        }
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
