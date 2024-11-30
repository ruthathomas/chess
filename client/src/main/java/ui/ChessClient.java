package ui;

import chess.*;
import model.*;
import exceptionhandling.ResponseException;
import requests.*;
import ui.clienthelpers.EscapeSequences;
import ui.clienthelpers.HelpStrings;

import java.util.*;

import static ui.clienthelpers.BoardBuilder.buildBoard;

public class ChessClient {
    private ServerFacade server;
    private int port;
    private AuthData authData;
    private GameData currGame;
    private ChessGame.TeamColor currColor;
    // in petShop, it has here a notif handler and a websocket facade
    private Status status = Status.LOGGEDOUT;
    //private Gson serializer = new Gson();
    //fixme I'm working on it

    public ChessClient(int port) {
        this.port = port;
        server = new ServerFacade(port);
        //probably you need to make a notification handler ;')
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
                case "register" -> {
                    return register(params);
                }
                case "login" -> {
                    return login(params);
                }
                case "logout" -> {
                    return logout();
                }
                case "list" -> {
                    return list();
                }
                case "create" -> {
                    return create(params);
                }
                case "join" -> {
                    return join(params);
                }
                case "observe" -> {
                    return observe(params);
                }
                case "redraw" -> {
                    return redraw();
                }
                case "leave" -> {
                    return "leave";
                }
                case "move" -> {
                    return "move";
                }
                case "resign" -> {
                    return "resign";
                }
                case "highlight" -> {
                    return highlight(params);
                }
                case "help" -> {
                    return help();
                }
                case "quit" -> {
                    if(status == Status.LOGGEDOUT) {
                        return "quit";
                    } else {
                       return "Error: must log out";
                    }
                }
                case null, default -> {
                    return help();
                }
            }
            //we can either check here that the state is correct, or do it within the function itself
        } catch (ResponseException ex) {
            //fixme
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
        // you should probably make it so people can't log out in the middle of a game
        server.logout(authData.authToken());
        status = Status.LOGGEDOUT;
        String result = String.format("Successfully logged out user %s", authData.username());
        authData = null;
        return result;
    }

    private String list() throws ResponseException {
        assertLoggedIn();
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
            buffer.append(String.format("ID: %d\t|\tName: %s\t\t|\tWhite: %s\t\t|\tBlack: %s%n",
                    gameNumber, value.gameName(),value.whiteUsername(), value.blackUsername()));
        }
        return buffer.toString();
    }

    private String create(String[] params) throws ResponseException {
        //you might want to make it so the names can have spaces? but I don't really want to
        assertLoggedIn();
        if(params.length > 0) {
            String name = String.join(" ", params);
            GameData game = server.createGame(authData.authToken(), name);
            return String.format("Successfully created game %s", game.gameName());
        }
        throw new ResponseException(400, "Error: expected game name");
    }

    private String join(String[] params) throws ResponseException {
        //FIXME when a player joins, must make a websocket connection
        assertLoggedIn();
        if(params.length > 1) {
            // this is the requested id; doesn't align with actual ids
            int id = Integer.parseInt(params[0]);
            var color = params[1].toLowerCase();
            if(color.equalsIgnoreCase("white")) {
                currColor = ChessGame.TeamColor.WHITE;
            } else {
                currColor = ChessGame.TeamColor.BLACK;
            }
            id = getIdFromRequestedId(id);
            server.joinGame(authData.authToken(), new JoinGameRequest(color, id));
            setCurrGame(id);
            status = Status.LOGGEDINPLAYING;
            if(currColor == ChessGame.TeamColor.WHITE) {
                return WordArt.ENTERING_GAME + getBoardString(ChessGame.TeamColor.WHITE, getEmptyHighlightArray());
            } else {
                return WordArt.ENTERING_GAME + getBoardString(ChessGame.TeamColor.BLACK, getEmptyHighlightArray());
            }
        }
        throw new ResponseException(400, "Error: expected game ID and player color");
    }

    private String observe(String[] params) throws ResponseException {
        //FIXME when a player joins, must make a websocket connection
        assertLoggedIn();
        if(params.length > 0) {
            int requestedId = Integer.parseInt(params[0]);
            setCurrGame(getIdFromRequestedId(requestedId));
            status = Status.LOGGEDINOBSERVING;
            return WordArt.ENTERING_GAME + getBoardString(ChessGame.TeamColor.WHITE, getEmptyHighlightArray());
        }
        throw new ResponseException(400, "Error: expected game ID");
    }

    private String redraw() throws ResponseException {
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
        return null;
    }

    private String move(String[] params) throws ResponseException {
        // takes start and end
        // will update for both users
        assertLoggedIn();
        if(params.length > 1) {
            String startRequest = params[0];
            int startCol = getIntFromChar(startRequest.charAt(0));
            int startRow = Integer.parseInt(String.valueOf(startRequest.charAt(1)));
            String endRequest = params[1];
            int endCol = getIntFromChar(endRequest.charAt(0));
            int endRow = Integer.parseInt(String.valueOf(endRequest.charAt(1)));
            //fixme continue
            String promotionRequest = params[2];
            ChessPiece.PieceType promotionPiece;
        }
        return null;
    }

    private String resign() throws ResponseException {
        assertLoggedIn();
        return null;
    }

    private String highlight(String[] params) throws ResponseException {
        assertLoggedIn();
        String positionRequest = params[0];
        try {
            int col = getIntFromChar(positionRequest.charAt(0));
            int row = Integer.parseInt(String.valueOf(positionRequest.charAt(1)));
            ChessBoard currBoard = currGame.game().getBoard();
            ChessPiece selectedPiece = currBoard.getPiece(new ChessPosition(row, col));
            Collection<ChessMove> moves =
                    selectedPiece.pieceMoves(currBoard, new ChessPosition(row, col));
            int[] highlightArray = getHighlightArray(moves);
            return "\n" + getBoardString(ChessGame.TeamColor.WHITE, highlightArray);
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

    private void setCurrGame(int requestedId) throws ResponseException {
        var games = server.listGames(authData.authToken()).games();
        for(var game : games) {
            if(game.gameID() == requestedId) {
                currGame = game;
                break;
            }
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

    private int[] getHighlightArray(Collection<ChessMove> moves) {
        int[] highlightArray = new int[64];
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        ChessPosition startPosition = null;
        for(var move : moves) {
            if (startPosition == null) {
                startPosition = new ChessPosition(move.getStartPosition().getRow() - 1,
                        move.getStartPosition().getColumn() - 1);
            }
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
            case 'a' -> {
                return  1;
            }
            case 'b' -> {
                return  2;
            }
            case 'c' -> {
                return  3;
            }
            case 'd' -> {
                return  4;
            }
            case 'e' -> {
                return  5;
            }
            case 'f' -> {
                return  6;
            }
            case 'g' -> {
                return  7;
            }
            case 'h' -> {
                return  8;
            }
            default -> throw new ResponseException(400, "Error: bad request");
        }
    }

}
