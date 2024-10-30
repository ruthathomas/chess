package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Map;

import static dataaccess.DatabaseManager.getConnection;

public class SQLDataAccess implements DataAccessInterface {

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        //
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void addAuth(AuthData authData) {
        //
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void addGame(GameData gameData) {
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
    public Map<Integer, GameData> getGames() {
        return null;
    }

    @Override
    public void clearAuthData() {
        deleteTableVals("auth");
    }

    @Override
    public void clearGameData() {
        deleteTableVals("game");
    }

    @Override
    public void clearUserData() {
        deleteTableVals("user");
    }

    private Object selectObject(String workingVar) {
        //FIXME I'm not sure what I'm doing
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("SELECT ?")) {
                preparedStatement.setString(1, workingVar);

                var rs = preparedStatement.executeQuery();
                rs.next();
                rs.getString(1);
            }
        } catch (DataAccessException | SQLException e) {
            //do something
        }
        return null;

//        private static void simpleParameter(int a, int b) throws SQLException {
//            try (var conn = getConnection()) {
//                try (var preparedStatement = conn.prepareStatement("SELECT ?+?")) {
//                    preparedStatement.setInt(1, a);
//                    preparedStatement.setInt(2, b);
//
//                    var rs = preparedStatement.executeQuery();
//                    rs.next();
//                    System.out.println(rs.getInt(1));
//                }
//            }
//        }

    }

    private void deleteTableVals(String table) {
        try(var conn = getConnection()) {
            try(var preparedStatement = conn.prepareStatement("DELETE FROM ?")) {
                preparedStatement.setString(1, table);
                preparedStatement.executeQuery();
                // I'm not sure if this is right?
            }
        } catch (DataAccessException | SQLException e) {
            // do something here ig
        }
    }

}
