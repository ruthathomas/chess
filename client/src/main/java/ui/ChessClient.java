package ui;

import com.google.gson.Gson;

import chess.*;
import model.*;
import exceptionhandling.ResponseException;
import requests.*;
import ui.clienthelpers.*;
import websocket.*;

import java.util.*;

import static ui.clienthelpers.BoardBuilder.buildBoard;

public class ChessClient {
    private final ServerFacade server;
    private final int port;
    private AuthData authData;
    private GameData currGame;
    private ChessGame.TeamColor currColor;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private Status status = Status.LOGGEDOUT;

    public ChessClient(int port, NotificationHandler notificationHandler) {
        this.port = port;
        server = new ServerFacade(port);
        this.notificationHandler = notificationHandler;
    }

    public String evaluateInput(String input) {
        try {
            var inputTokens = input.split(" ");
            String cmd = "help";
            var params = Arrays.copyOfRange(inputTokens, 1, inputTokens.length);
            if(inputTokens.length > 0) {
                cmd = inputTokens[0];
            }
            switch(cmd) {
                case "register" -> { return register(params); }
                case "login" -> { return login(params); }
                case "logout" -> { return logout(); }
                case "list" -> { return list(); }
                case "create" -> { return create(params); }
                case "join" -> { return join(params); }
                case "observe" -> { return observe(params); }
                case "redraw" -> { return redraw(); }
                case "leave" -> { return leave(); }
                case "move" -> { return move(params); }
                case "resign" -> { return resign(); }
                case "highlight" -> { return highlight(params); }
                case "quit" -> {
                    if(status == Status.LOGGEDOUT) {
                        return "quit";
                    } else {
                       return "Error: must log out";
                    }
                }
                case null, default -> { return help(); }
            }
            //we can either check here that the state is correct, or do it within the function itself
        } catch (Exception ex) {
            if(ex.getMessage() == null) {
                // to be honest, I don't know why this is here?? but okay
                return "ERROR: invalid move request.";
            }
            return ex.getMessage();
        }
    }

    public String help() {
        switch (status) {
            case LOGGEDOUT -> {
                return HelpStrings.LOGGED_OUT_HELP;
            }
            case LOGGEDINIDLE -> {
                return HelpStrings.LOGGED_IN_HELP;
            }
            case LOGGEDINPLAYING -> {
                return HelpStrings.PLAYING_HELP;
            }
            case LOGGEDINOBSERVING -> {
                return HelpStrings.OBSERVING_HELP;
            }
            case null, default -> {
                // this shouldn't cause problems, because there shouldn't be something with another status
                return null;
            }
        }
    }

    public String getStatus() {
        return status.toString();
    }

    private String register(String[] params) throws ResponseException {
        assertLoggedOut();
        if(params.length > 2) {
            UserData user = new UserData(params[0], params[1], params[2]);
            authData = server.register(user);
            status = Status.LOGGEDINIDLE;
            return String.format("Successfully registered user %s", authData.username());
        }
        throw new ResponseException(400, "Error: expected username, password, and email");
    }

    private String login(String[] params) throws ResponseException {
        assertLoggedOut();
        if(params.length > 1) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            authData = server.login(loginRequest);
            status = Status.LOGGEDINIDLE;
            return String.format("Successfully logged in user %s", authData.username());
        }
        throw new ResponseException(400, "Error: expected username and password");
    }

    private String logout() throws ResponseException {
        assertLoggedIn();
        assertNotPlaying();
        assertNotObserving();
        ws = null;
        server.logout(authData.authToken());
        status = Status.LOGGEDOUT;
        String result = String.format("Successfully logged out user %s", authData.username());
        authData = null;
        return result;
    }

    private String list() throws ResponseException {
        assertLoggedIn();
        assertNotPlaying();
        assertNotObserving();
        var games = server.listGames(authData.authToken());
        Map<Integer, GameData> gamesMap = new HashMap<>();
        for(var game : games.games()) {
            gamesMap.put(game.gameID(), game);
        }
        var buffer = new StringBuilder();
        int gameNumber = 0;
        for(var game : gamesMap.entrySet()) {
            gameNumber += 1;
            var value = game.getValue();
            if(gameNumber > 1) {
                buffer.append("\t");
            }
            String ended;
            if(value.game().isOver()) {
                ended = "Yes";
            } else {
                ended = "No";
            }
            buffer.append(String.format("ID: %d\t|\tName: %s\t\t|\tWhite: %s\t\t|\tBlack: %s\t\t|\tEnded: %s%n",
                    gameNumber, value.gameName(),value.whiteUsername(), value.blackUsername(), ended));
        }
        return buffer.toString();
    }

    private String create(String[] params) throws ResponseException {
        assertLoggedIn();
        assertNotPlaying();
        assertNotObserving();
        if(params.length > 0) {
            String name = String.join(" ", params);
            GameData game = server.createGame(authData.authToken(), name);
            return String.format("Successfully created game %s", game.gameName());
        }
        throw new ResponseException(400, "Error: expected game name");
    }

    private String join(String[] params) throws ResponseException {
        assertLoggedIn();
        assertNotPlaying();
        assertNotObserving();
        if(params.length > 1) {
            // this is the requested id
            int id;
            var param0 = params[0];
            if(!param0.isEmpty()) {
                try {
                    id = Integer.parseInt(param0);
                } catch (NumberFormatException e) {
                    throw new ResponseException(400, "Error: invalid game id provided.");
                }
            } else {
                throw new ResponseException(400, "Error: invalid game id provided.");
            }
            var color = params[1].toLowerCase();
            if(color.equalsIgnoreCase("white")) {
                currColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                currColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException(400, "Error: bad color request");
            }
            id = getIdFromRequestedId(id);
            server.joinGame(authData.authToken(), new JoinGameRequest(color, id));
            setCurrGame(id);
            status = Status.LOGGEDINPLAYING;
            ws = new WebSocketFacade(port, notificationHandler);
            ws.connectToGame(authData, id);
            return WordArt.ENTERING_GAME;
        }
        throw new ResponseException(400, "Error: expected game ID and player color");
    }

    private String observe(String[] params) throws ResponseException {
        ws = new WebSocketFacade(port, notificationHandler);
        assertLoggedIn();
        assertNotPlaying();
        assertNotObserving(); // you must leave a game before you can start observing another
        if(params.length > 0) {
            int requestedId = Integer.parseInt(params[0]);
            int id = getIdFromRequestedId(requestedId);
            setCurrGame(id);
            status = Status.LOGGEDINOBSERVING;
            ws.connectToGame(authData, id);
            return WordArt.ENTERING_GAME;
        }
        throw new ResponseException(400, "Error: expected game ID");
    }

    private String redraw() throws ResponseException {
        setCurrGame(currGame.gameID());
        assertLoggedIn();
        // null indicates no color assigned; observing
        if(currColor == null || currColor == ChessGame.TeamColor.WHITE) {
            return "\n" + getBoardString(ChessGame.TeamColor.WHITE, getEmptyHighlightArray());
        } else {
            return "\n" + getBoardString(ChessGame.TeamColor.BLACK, getEmptyHighlightArray());
        }
    }

    private String leave() throws ResponseException {
        assertLoggedIn();
        if(status == Status.LOGGEDINIDLE) {
            throw new ResponseException(400, "You can't leave a game if you aren't in one.");
        }
        setCurrGame(currGame.gameID());
        ws.leaveGame(authData, currGame.gameID());
        currGame = null;
        status = Status.LOGGEDINIDLE;

        return WordArt.EXITING_GAME;
    }

    private String move(String[] params) throws ResponseException {
        // make sure parameters don't cause an error; must have two characters
        assertLoggedIn();
        assertPlaying();
        //assertYourTurn();
        try {
            if(params.length > 1) {
                String startRequest = params[0];
                if(startRequest.length() < 2) {
                    return "Error: bad starting square.";
                }
                int startCol = getIntFromChar(startRequest.charAt(0));
                int startRow = Integer.parseInt(String.valueOf(startRequest.charAt(1)));
                if(currGame.game().getBoard().getPiece(new ChessPosition(startRow, startCol)) == null) {
                    return "Error: starting square is empty.";
                }
                String endRequest = params[1];
                if(endRequest.length()< 2) {
                    return "Error: bad ending request.";
                }
                int endCol = getIntFromChar(endRequest.charAt(0));
                int endRow = Integer.parseInt(String.valueOf(endRequest.charAt(1)));
                String piece = currGame.game().getBoard().getPiece(new ChessPosition(startRow, startCol)).getPieceType().toString();
                // HEY, LISTEN: maybe something in here about if a piece was captured?/etc.
                String moveString = String.format("a %s from %s to %s", piece, startRequest, endRequest);
                String promotionRequest;
                ChessPiece.PieceType promotionPiece = null;
                if (params.length > 2) {
                    promotionRequest = params[2];
                    promotionPiece = getPieceFromString(promotionRequest);
                    moveString += " and promoted their pawn to a " + promotionRequest;
                }
                ChessMove moveRequest = new ChessMove(new ChessPosition(startRow, startCol),
                        new ChessPosition(endRow, endCol), promotionPiece);
                // get rid of this for next time
                //currGame.game().makeMove(moveRequest);
                ws.makeMove(authData, currGame.gameID(), moveRequest);
                // add update of game here
                setCurrGame(currGame.gameID());
            }
            return "";
        } catch (Exception e) {
            // well this isn't right (I don't think); why am I assuming bad request?
            throw new ResponseException(400, e.getMessage());
        }
    }

    private String resign() throws ResponseException {
        assertLoggedIn();
        assertPlaying();
        setCurrGame(currGame.gameID());
        ws.resignFromGame(authData, currGame.gameID());
        return "To leave the game, enter 'leave'.";
    }

    private String highlight(String[] params) throws ResponseException {
        assertLoggedIn();
        String positionRequest = params[0];
        try {
            if(positionRequest.length() < 2) { throw new ResponseException(400, "Error: bad square request."); }
            int col = getIntFromChar(positionRequest.charAt(0));
            int row = Integer.parseInt(String.valueOf(positionRequest.charAt(1)));
            ChessBoard currBoard = currGame.game().getBoard();
            ChessPiece selectedPiece = currBoard.getPiece(new ChessPosition(row, col));
            Collection<ChessMove> moves = currGame.game().validMoves(new ChessPosition(row, col));
//                    selectedPiece.pieceMoves(currBoard, new ChessPosition(row, col));
            int[] highlightArray = getHighlightArray(moves, row - 1, col - 1);
            // this needs to be addressed; do they always want it from one perspective? if it doesn't incl start sq, add it!!
            return "\n" + getBoardString(selectedPiece.getTeamColor(), highlightArray);
        } catch (Exception e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    private void assertLoggedIn() throws ResponseException {
        if(status == Status.LOGGEDOUT) {
            throw new ResponseException(400, "Error: must first log in");
        }
    }

    private void assertLoggedOut() throws ResponseException {
        if(status != Status.LOGGEDOUT) {
            throw new ResponseException(400, "Error: must first log out");
        }
    }

    private void assertPlaying() throws ResponseException {
        if(status != Status.LOGGEDINPLAYING) {
            throw new ResponseException(400, "Error: must first join a game");
        }
    }

    private void assertNotPlaying() throws ResponseException {
        if(status == Status.LOGGEDINPLAYING) {
            throw new ResponseException(400, "Error: must first exit the game");
        }
    }

    private void assertNotObserving() throws ResponseException {
        if(status == Status.LOGGEDINOBSERVING) {
            throw new ResponseException(400, "Error: must first exit the game");
        }
    }

//    private void assertYourTurn() throws ResponseException {
//        if(currGame.game().getTeamTurn() != currColor) {
//            throw new ResponseException(400, "It is not your turn.");
//        }
//    }

    private int getIdFromRequestedId(int requestedId) throws ResponseException {
        int trueId = 0;
        var games = server.listGames(authData.authToken()).games();
        int gameNumber = 0;
        // get the proper number for the game
        for(var game : games) {
            gameNumber += 1;
            if(gameNumber == requestedId) {
                // the requested game has been found; set trueId equal to its gameID
                trueId = game.gameID();
                break;
            }
        }
        // if trueId is still 0, a client-side failure has occurred
        if(trueId == 0) {
            throw new ResponseException(400, "Error: bad request");
        }
        return trueId;
    }

    // THERE APPEARS TO BE SOME SERVER ERROR OR SOMETHING OCCURING IN THE SETCURRGAME FUNCTION SO LOOK AT THAT
    private void setCurrGame(int requestedId) throws ResponseException {
        try {
            var games = server.listGames(authData.authToken()).games();
            for(var game : games) {
                if(game.gameID() == requestedId) {
                    currGame = game;
                    break;
                }
            }
        } catch (ResponseException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String getBoardString(ChessGame.TeamColor color, int[] highlightArray) {
        String[] pieceArray = getPieceArray();
        return buildBoard(pieceArray, highlightArray, color);
    }

    private String[] getPieceArray() {
        ChessBoard currBoard = currGame.game().getBoard();
        String[] pieceArray = new String[64];
        int currCell = 0;
        for(int i = 8; i > 0; i--) {
            for(int j = 1; j < 9; j++) {
                ChessPiece currPiece = currBoard.getPiece(new ChessPosition(i,j));
                if(currPiece == null) {
                    pieceArray[currCell] = EscapeSequences.EMPTY;
                    currCell +=1;
                    continue;
                }
                ChessPiece.PieceType pieceType = currPiece.getPieceType();
                if(currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    switch (pieceType) {
                        case KING -> pieceArray[currCell] = EscapeSequences.WHITE_KING;
                        case QUEEN -> pieceArray[currCell] = EscapeSequences.WHITE_QUEEN;
                        case BISHOP -> pieceArray[currCell] = EscapeSequences.WHITE_BISHOP;
                        case KNIGHT -> pieceArray[currCell] = EscapeSequences.WHITE_KNIGHT;
                        case ROOK -> pieceArray[currCell] = EscapeSequences.WHITE_ROOK;
                        case PAWN -> pieceArray[currCell] = EscapeSequences.WHITE_PAWN;
                    }
                } else {
                    switch (pieceType) {
                        case KING -> pieceArray[currCell] = EscapeSequences.BLACK_KING;
                        case QUEEN -> pieceArray[currCell] = EscapeSequences.BLACK_QUEEN;
                        case BISHOP -> pieceArray[currCell] = EscapeSequences.BLACK_BISHOP;
                        case KNIGHT -> pieceArray[currCell] = EscapeSequences.BLACK_KNIGHT;
                        case ROOK -> pieceArray[currCell] = EscapeSequences.BLACK_ROOK;
                        case PAWN -> pieceArray[currCell] = EscapeSequences.BLACK_PAWN;
                    }
                }
                currCell +=1;
            }
        }
        return pieceArray;
    }

    private int[] getHighlightArray(Collection<ChessMove> moves, int startRow, int startCol) {
        int[] highlightArray = new int[64];
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for(var move : moves) {
            ChessPosition endPosition = new ChessPosition(move.getEndPosition().getRow() - 1,
                    move.getEndPosition().getColumn() - 1);
            endPositions.add(endPosition);
        }
        int currCell = 0;
        for(int i = 8; i > 0; i--) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPos = new ChessPosition(i - 1, j - 1);
                if(Objects.equals(startPosition, currPos)) {
                    highlightArray[currCell] = 2;
                } else if(endPositions.contains(currPos)) {
                    highlightArray[currCell] = 1;
                } else {
                    highlightArray[currCell] = 0;
                }
                currCell +=1;
            }
        }
        return highlightArray;
    }

    private int[] getEmptyHighlightArray() {
        int[] highlightArray = new int[64];
        for(int i = 0; i < 64; i++) {
            highlightArray[i] = 0;
        }
        return highlightArray;
    }

    private int getIntFromChar(char c) throws ResponseException {
        switch (c) {
            case 'a' -> { return  1; }
            case 'b' -> { return  2; }
            case 'c' -> { return  3; }
            case 'd' -> { return  4; }
            case 'e' -> { return  5; }
            case 'f' -> { return  6; }
            case 'g' -> { return  7; }
            case 'h' -> { return  8; }
            default -> throw new ResponseException(400, "Error: bad request");
        }
    }

    // there's no checking here for okayness bc that happens in the game logic
    ChessPiece.PieceType getPieceFromString(String piece) {
        piece = piece.toLowerCase();
        switch (piece) {
            case "king" -> { return ChessPiece.PieceType.KING; }
            case "queen" -> { return ChessPiece.PieceType.QUEEN; }
            case "rook" -> { return ChessPiece.PieceType.ROOK; }
            case "bishop" -> { return ChessPiece.PieceType.BISHOP; }
            case "knight" -> { return ChessPiece.PieceType.KNIGHT; }
            case "pawn" -> { return ChessPiece.PieceType.PAWN; }
            default -> { return null; }
        }
    }

}
