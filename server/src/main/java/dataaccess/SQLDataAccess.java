package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import server.ResponseException;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class SQLDataAccess implements DataAccessInterface {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private Gson serializer = new Gson();

    private final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken varchar(255) NOT NULL,
            username varchar(255) NOT NULL,
            PRIMARY KEY (authToken)
            )
"""
    };

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            username varchar(255) NOT NULL,
            password varchar(255) NOT NULL,
            email varchar(255) NOT NULL,
            id int NOT NULL AUTO_INCREMENT,
            PRIMARY KEY (id),
            INDEX (username)
            )
"""
    };
    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS USER (
            gameID int NOT NULL,
            whiteUsername varchar(255) NOT NULL,
            blackUsername varchar(255) NOT NULL,
            gameName varchar(255) NOT NULL,
            game longtext NOT NULL,
            id int NOT NULL AUTO_INCREMENT,
            PRIMARY KEY (id),
            INDEX (username)
            )
"""
    };

    enum Query {
        ADD_AUTH,
        ADD_GAME,
        ADD_USER,
        SELECT_AUTH,
        SELECT_GAME,
        SELECT_USER
    }

    enum TableName {
        AUTH,
        GAME,
        USER
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
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.execute();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return (GameData) singleSelection(Query.SELECT_GAME, "", gameID);
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        valueInsertion(Query.ADD_GAME, gameData);
        // 1. You're going to have to serialize the chess game to a JSON string
        // 2. and then you're going to have to put it in that database
    }

    @Override
    public GameData updateGame(int gameID, GameData gameData) throws DataAccessException {
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername = ?, blackUsername = ?," +
                    " gameName = ?, game = ? WHERE gameID = ?")) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, serializer.toJson(gameData.game()));
                preparedStatement.setInt(5, gameID);
                preparedStatement.execute();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameData;
    }

    @Override
    public Map<Integer, GameData> getGames() throws DataAccessException {
        Map<Integer, GameData> gamesList = new HashMap<>();
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("SELECT COUNT(*) FROM game")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                int gamesCount = rs.getInt("count(*)");
                for(int i = 1; i <= gamesCount; i++) {
                    var nextStatement = conn.prepareStatement("SELECT * FROM game WHERE id = ?");
                    nextStatement.setInt(1, i);
                    var rs2 = nextStatement.executeQuery();
                    rs2.next();
                    var game = serializer.fromJson(rs2.getString("game"), ChessGame.class);
                    gamesList.put(rs2.getInt("gameID"), new GameData(rs2.getInt("gameID"),
                            rs2.getString("whiteUsername"), rs2.getString("blackUsername"),
                            rs2.getString("gameName"), game));
                }
                // I'm not sure if this is right?
                return gamesList;
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        deleteTableVals(TableName.AUTH);
    }

    @Override
    public void clearGameData() throws DataAccessException {
        deleteTableVals(TableName.GAME);
    }

    @Override
    public void clearUserData() throws DataAccessException {
        deleteTableVals(TableName.USER);
    }

    private void deleteTableVals(TableName table) throws DataAccessException {
        String queryString;
        switch(table) {
            case AUTH -> queryString = "TRUNCATE auth";
            case GAME -> queryString = "TRUNCATE game";
            case USER -> queryString = "TRUNCATE user";
            case null, default -> throw new DataAccessException("Unexpected query.");
        }
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement(queryString)) {
                preparedStatement.execute();
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
                if(!rs.next()) {
                    // if results set is empty, return null
                    return null;
                }
                rs.next();
                //Function is now on the proper line to select items
                switch (query) {
                    case SELECT_AUTH -> {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                    case SELECT_GAME -> {
                        var game = serializer.fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"), game);
                        //deserialize the string that the game is and then make a new GameData object
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
                    var gameString = serializer.toJson(((GameData) dataObject).game());
                    if(gameString.equals("null")) {
                        throw new DataAccessException("Insertion failed; cannot insert a null value");
                    }
                    preparedStatement.setString(5, gameString);
                } else if(dataObject instanceof UserData && query == Query.ADD_USER) {
                    preparedStatement.setString(1, ((UserData) dataObject).username());
                    preparedStatement.setString(2, ((UserData) dataObject).password());
                    preparedStatement.setString(3, ((UserData) dataObject).email());
                } else {
                    throw new DataAccessException("Unexpected data used for query.");
                }
                preparedStatement.execute();
                // I'm not sure if this is right?
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()) {
            try(var preparedStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS chess")) {
                preparedStatement.executeUpdate();
            }
            try(var preparedStatement = conn.prepareStatement("USE chess")) {
                preparedStatement.executeUpdate();
            }
            for(var statement : createAuthStatements) {
                try(var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for(var statement : createGameStatements) {
                try(var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for(var statement : createUserStatements) {
                try(var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

}
