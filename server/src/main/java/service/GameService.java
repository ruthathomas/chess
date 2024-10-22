package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    private MemoryDataAccess memoryDataAccess;

    public GameService(MemoryDataAccess memDA) {
        memoryDataAccess = memDA;
    }

    public ArrayList<GameData> listGames() {
        return memoryDataAccess.getGames();
    }

    public GameData createGame(String gameName) {
        GameData newGame = new GameData(generateGameID(), "", "", gameName, new ChessGame());
        memoryDataAccess.addGame(newGame);
        return newGame;
    }

    //FIXME
    public void joinGame(int gameID, String playerColor, String username) {
        GameData currGame = memoryDataAccess.getGame(gameID);
        playerColor = playerColor.toLowerCase();
        try {
            if(playerColor == "white") {
                if(currGame.whiteUsername() == "") {
                    memoryDataAccess.updateGame(gameID, new GameData(gameID, username, currGame.blackUsername(), currGame.gameName(), currGame.game()));
                } else {
                    //fixme throw an error for bad request
                }
            } else {
                if(currGame.blackUsername() == "") {
                    memoryDataAccess.updateGame(gameID, new GameData(gameID, currGame.whiteUsername(), username, currGame.gameName(), currGame.game()));
                } else {
                    //fixme throw an error for bad request
                }
            }
        } catch (dataaccess.DataAccessException e) {
            //FIXME DO A SERVICE ERROR HERE;
        }
    }

    public void clearData() {
        try {
            memoryDataAccess.clearData();
        } catch (dataaccess.DataAccessException e) {
            //FIXME do something with the error
        }
    }

    //FIXME this is slapdash garbage
    private int generateGameID() {
        int currID = 0;
        for(var game : memoryDataAccess.getGames()) {
            if(game.gameID() > currID) {
                currID = game.gameID() + 1;
            }
        }
        return currID;
    }
}
