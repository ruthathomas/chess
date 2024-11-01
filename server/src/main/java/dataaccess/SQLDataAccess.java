package dataaccess;

import model.*;

import java.sql.SQLException;
import java.util.Map;

import static dataaccess.DatabaseManager.getConnection;

public class SQLDataAccess implements DataAccessInterface {

    enum Query {
        ADD_AUTH,
        ADD_GAME,
        ADD_USER,
        DELETE_AUTH_TOKEN,
        DELETE_TABLE,
        SELECT_ALL_GAMES,
        SELECT_AUTH,
        SELECT_GAME,
        SELECT_USER
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return (UserData) singleSelection(Query.SELECT_USER, username, 0);

    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        valueInsertion(Query.ADD_USER, userData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return (AuthData) singleSelection(Query.SELECT_AUTH, authToken, 0);
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        valueInsertion(Query.ADD_AUTH, authData);
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        //FIXME
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return (GameData) singleSelection(Query.SELECT_GAME, "", gameID);
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        // 1. You're going to have to serialize the chess game to a JSON string
        // 2. and then you're going to have to put it in that database
        //
    }

    @Override
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException {
//        1. Select the game’s state (JSON string) from the database
//        2. Deserialize the JSON string to a ChessGame Java object
//        3. Update the state of the ChessGame object
//        4. Re-serialize the Chess game to a JSON string
//        5. Update the game’s JSON string in the database
        return null;
    }

    @Override
    public Map<Integer, GameData> getGames() throws DataAccessException {
        //FIXME
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        deleteTableVals("auth");
    }

    @Override
    public void clearGameData() throws DataAccessException {
        deleteTableVals("game");
    }

    @Override
    public void clearUserData() throws DataAccessException {
        deleteTableVals("user");
    }

    private void deleteTableVals(String table) throws DataAccessException {
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("DELETE FROM ?")) {
                preparedStatement.setString(1, table);
                preparedStatement.executeQuery();
                // I'm not sure if this is right?
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private Object singleSelection(Query query, String queryStrVal, int queryIntVal) throws DataAccessException {
        String queryStatement;
        switch (query) {
            case SELECT_AUTH -> queryStatement = "SELECT * FROM auth WHERE authToken = ?";
            case SELECT_GAME -> queryStatement = "SELECT * FROM game WHERE gameID = ?";
            case SELECT_USER -> queryStatement = "SELECT * FROM user WHERE username = ?";
            case null, default -> queryStatement = "";
        }
        try(var conn = getConnection()) {
            if(queryStatement == "") {
                throw new DataAccessException("Unexpected query made.");
            }
            try(var preparedStatement = conn.prepareStatement(queryStatement)) {
                if(queryStrVal != "" && queryStrVal != null) {
                    preparedStatement.setString(1, queryStrVal);
                } else if(queryIntVal != 0) {
                    preparedStatement.setInt(1, queryIntVal);
                }
                var rs = preparedStatement.executeQuery();
                rs.next();
                //Function is now on the proper line to select items
                switch (query) {
                    case SELECT_AUTH -> {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                    case SELECT_GAME -> {
                        //deserialize the string that the game is and then make a new GameData object
                        return "lol fixme";
                    }
                    case SELECT_USER -> {
                        // split into two ugly lines because of java conventions :(
                        return new UserData(rs.getString("username"), rs.getString("password"),
                                rs.getString("email"));
                    }
                    case null, default -> throw new DataAccessException("Unexpected query made.");
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void valueInsertion(Query query, Object dataObject) throws DataAccessException {
        String queryStatement;
        switch (query) {
            case ADD_AUTH -> queryStatement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            case ADD_GAME -> {
                queryStatement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?)";
            }
            case ADD_USER -> queryStatement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            case null, default -> throw new DataAccessException("Unexpected query made.");
        }
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement(queryStatement)) {
                if(dataObject instanceof AuthData && query == Query.ADD_AUTH) {
                 preparedStatement.setString(1, ((AuthData) dataObject).authToken());
                 preparedStatement.setString(2, ((AuthData) dataObject).username());
                } else if (dataObject instanceof GameData && query == Query.ADD_GAME) {
                    preparedStatement.setInt(1, ((GameData) dataObject).gameID());
                    preparedStatement.setString(2, ((GameData) dataObject).whiteUsername());
                    preparedStatement.setString(3, ((GameData) dataObject).blackUsername());
                    preparedStatement.setString(4, ((GameData) dataObject).gameName());
                    //FIXME here is where you need to do the json conversion
                    preparedStatement.setString(5, "FIXME FIXME");
                } else if(dataObject instanceof UserData && query == Query.ADD_USER) {
                    preparedStatement.setString(1, ((UserData) dataObject).username());
                    preparedStatement.setString(2, ((UserData) dataObject).password());
                    preparedStatement.setString(3, ((UserData) dataObject).email());
                } else {
                    throw new DataAccessException("Unexpected query made.");
                }
                preparedStatement.executeQuery();
                // I'm not sure if this is right?
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
