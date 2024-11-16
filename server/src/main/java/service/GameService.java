package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import model.AuthData;
import model.GameData;
import exceptionhandling.ResponseException;

import java.util.Map;

public class GameService {

    private final DataAccessInterface dataAccess;

    public GameService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Map<Integer, GameData> listGames(String authToken) throws ResponseException {
        try {
            if(dataAccess.getAuth(authToken) == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            return dataAccess.getGames();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public GameData createGame(String gameName, String authToken) throws ResponseException {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if(authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            GameData newGame = new GameData(generateGameID(), null, null, gameName,
                    new ChessGame());
            dataAccess.addGame(newGame);
            return newGame;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ResponseException {
        AuthData authData;
        GameData currGame;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
        if(authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            currGame = dataAccess.getGame(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
        if(currGame == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (playerColor == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if(authData.username().equals(currGame.whiteUsername()) ||
                authData.username().equals(currGame.blackUsername())) {
            throw new ResponseException(400, "Error: bad request");
        }
        playerColor = playerColor.toLowerCase();
        try {
            if(playerColor.equals("white")) {
                if(currGame.whiteUsername() == null || currGame.whiteUsername() == "") {
                    dataAccess.updateGame(gameID, new GameData(gameID, authData.username(),
                            currGame.blackUsername(), currGame.gameName(), currGame.game()));
                } else {
                    throw new ResponseException(403, "Error: already taken");
                }
            } else {
                if(currGame.blackUsername() == null || currGame.blackUsername() == "") {
                    dataAccess.updateGame (gameID, new GameData(gameID, currGame.whiteUsername(),
                            authData.username(), currGame.gameName(), currGame.game()));
                } else {
                    throw new ResponseException(403, "Error: already taken");
                }
            }
        } catch (dataaccess.DataAccessException e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearGameData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // To be used only in testing for the adding of "pre-existing" data to the server
    public void addGame(GameData gameData) throws ResponseException{
        try {
            dataAccess.addGame(gameData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // I think I'd like to change this in future
    private int generateGameID() throws ResponseException{
        try {
            int currID = 1;
            for (var key : dataAccess.getGames().keySet()) {
                if (key >= currID) {
                    currID = key + 1;
                }
            }
            return currID;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
