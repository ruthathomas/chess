package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import server.ResponseException;

import java.util.Map;

public class GameService {

    private MemoryDataAccess memoryDataAccess;

    public GameService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

    public Map<Integer, GameData> listGames(String authToken) throws ResponseException {
        if(memoryDataAccess.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return memoryDataAccess.getGames();
    }

    public GameData createGame(String gameName, String authToken) throws ResponseException {
        AuthData authData = memoryDataAccess.getAuth(authToken);
        if(authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        //FIXME this is also supposed to have a bad request error
        GameData newGame = new GameData(generateGameID(), null, null, gameName, new ChessGame());
        memoryDataAccess.addGame(newGame);
        return newGame;
    }

    //FIXME
    public void joinGame(int gameID, String playerColor, String authToken) throws ResponseException {
        AuthData authData = memoryDataAccess.getAuth(authToken);
        if(authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        GameData currGame = memoryDataAccess.getGame(gameID);
        if(currGame == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        playerColor = playerColor.toLowerCase();
        try {
            if(playerColor.equals("white")) {
                if(currGame.whiteUsername() == null) {
                    memoryDataAccess.updateGame(gameID, new GameData(gameID, authData.username(), currGame.blackUsername(), currGame.gameName(), currGame.game()));
                } else {
                    throw new ResponseException(403, "Error: already taken");
                }
            } else {
                if(currGame.blackUsername() == null) {
                    memoryDataAccess.updateGame(gameID, new GameData(gameID, currGame.whiteUsername(), authData.username(), currGame.gameName(), currGame.game()));
                } else {
                    throw new ResponseException(403, "Error: already taken");
                }
            }
        } catch (dataaccess.DataAccessException e) {
            //FIXME this is a bad exception to throw and you need to care about it
            throw new ServiceException("FIXME something else happened and it failed");
        }
    }

    public void clearData() throws ServiceException {
        memoryDataAccess.clearGameData();
        // Throws an exception if something wonky happens
    }

    /**
     * To be used only in testing for the adding of "pre-existing" data to the server
     * @param gameData the data to add
     */
    public void addGame(GameData gameData) {
        memoryDataAccess.addGame(gameData);
    }

    //FIXME this is slapdash garbage
    private int generateGameID() {
        int currID = 1;
        for(var key : memoryDataAccess.getGames().keySet()) {
            if(key >= currID) {
                currID = key + 1;
            }
        }
        return currID;
    }
}
