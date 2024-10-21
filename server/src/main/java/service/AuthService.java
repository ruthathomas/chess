package service;

import java.util.UUID;

import dataaccess.DataAccessInterface;
import dataaccess.MemoryDataAccess;
import model.AuthData;

public class AuthService {

    private DataAccessInterface authMemory = new MemoryDataAccess();
   // ALSO in the works
//    public void createAuth(AuthData auth) {}
//    private void delAuth(AuthData auth) {}

    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData newAuth = new AuthData(authToken, username);
        authMemory.addAuth(newAuth);
        return newAuth;
    }

    // It feels dumb to have this function that just calls another function, but
    public AuthData getAuth(String authToken) {
        return authMemory.getAuth(authToken);
    }

    public void delAuth(String authToken) {
        authMemory.delAuth(authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
