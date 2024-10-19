package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccessInterface {
    public UserData getUser(String username);
    public AuthData createUser(UserData userData);
    public AuthData createAuth(AuthData authData);
}
