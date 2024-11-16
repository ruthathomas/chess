package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import model.*;
import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;
import server.requests.JoinGameRequest;
import server.requests.LoginRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    private ServerFacade server;
    private int port;
    private AuthData authData;
    private GameData currGame;
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
                case "help" -> {
                    return help();
                }
                case "quit" -> {
                    return "quit";
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
        if(status == Status.LOGGEDOUT) {
            return """
                    \u001b[1m\u001b[38;5;5m\t[OPTIONS]
                    \t┝ register <USERNAME> <PASSWORD> <EMAIL> - \u001b[22m\u001b[38;5;242mto create an account
                    \u001b[1m\u001b[38;5;5m\t┝ login <USERNAME> <PASSWORD> - \u001b[22m\u001b[38;5;242mto play chess
                    \u001b[1m\u001b[38;5;5m\t┝ quit - \u001b[22m\u001b[38;5;242mto exit the program
                    \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
                    """;
        } else if (status == Status.LOGGEDINIDLE) {
            return """
                    \u001b[1m\u001b[38;5;5m\t[OPTIONS]
                    \t┝ create <NAME> - \u001b[22m\u001b[39mto create a game
                    \u001b[1m\u001b[38;5;5m\t┝ list - \u001b[22m\u001b[38;5;242mto list all games
                    \u001b[1m\u001b[38;5;5m\t┝ join <ID> [WHITE|BLACK] - \u001b[22m\u001b[38;5;242mto join a game
                    \u001b[1m\u001b[38;5;5m\t┝ observe <ID> - \u001b[22m\u001b[38;5;242mto observe a game
                    \u001b[1m\u001b[38;5;5m\t┝ logout - \u001b[22m\u001b[38;5;242mto logout of the program
                    \u001b[1m\u001b[38;5;5m\t┝ quit - \u001b[22m\u001b[38;5;242mto exit the program
                    \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
                    """;
        } else {
            return """
                    don't worry about it for now ;')
                    I'll make separate ones for observing and playing
                    """;
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
            //fixme
        }
        throw new ResponseException(400, "Error: bad request (expected username, password, and email)");
    }

    private String login(String[] params) throws ResponseException {
        assertLoggedOut();
        if(params.length > 1) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            authData = server.login(loginRequest);
            //fixme???
            status = Status.LOGGEDINIDLE;
            return String.format("Successfully logged in user %s", authData.username());
        }
        throw new ResponseException(400, "Error: bad request (expected username and password)");
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
            buffer.append(String.format("ID: %d\t|\tName: %s\t|\tWhite: %s\t|\tBlack: %s%n", gameNumber, value.gameName(),
                    value.whiteUsername(), value.blackUsername()));
        }
        return buffer.toString();
    }

    private String create(String[] params) throws ResponseException {
        //you might want to make it so the names can have spaces? but I don't really want to
        assertLoggedIn();
        if(params.length > 0) {
            GameData game = server.createGame(authData.authToken(), params[0]);
            return String.format("Successfully created game %s", game.gameName());
        }
        throw new ResponseException(400, "Error: bad request (expected game name)");
    }

    private String join(String[] params) throws ResponseException {
        assertLoggedIn();
        if(params.length > 1) {
            // this is the requested id; doesn't align with actual ids
            int id = Integer.parseInt(params[0]);
            var color = params[1].toLowerCase();
            id = getIdFromRequestedId(id);
            server.joinGame(authData.authToken(), new JoinGameRequest(color, id));
            setCurrGame(id);
            status = Status.LOGGEDINPLAYING;
            //FIXME FIXME THIS IS TEMPORARY
            return getBoardString("white") + "\n\n" + getBoardString("black");
        }
        throw new ResponseException(400, "Error: bad request (expected game ID and player color)");
    }

    private String observe(String[] params) throws ResponseException {
        assertLoggedIn();
        if(params.length > 0) {
            int requestedId = Integer.parseInt(params[0]);
            setCurrGame(getIdFromRequestedId(requestedId));
            status = Status.LOGGEDINOBSERVING;
            //FIXME FIXME THIS IS TEMPORARY
            return getBoardString("white") + "\n\n" + getBoardString("black");
        }
        throw new ResponseException(400, "Error: bad request (expected game ID)");
    }

    private void assertLoggedIn() throws ResponseException {
        if(status == Status.LOGGEDOUT) {
            throw new ResponseException(400, "Error: bad request (must log in)");
        }
    }

    private void assertLoggedOut() throws ResponseException {
        if(status != Status.LOGGEDOUT) {
            throw new ResponseException(400, "Error: bad request (must log out)");
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

    private String getBoardString(String color) {
        ChessBoard board = currGame.game().getBoard();
        //FIXME! FIXME!
        return board.toString();
    }
}
