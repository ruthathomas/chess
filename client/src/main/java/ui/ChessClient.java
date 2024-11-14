package ui;

import com.google.gson.Gson;
import model.AuthData;
import server.ResponseException;
import server.requests.LoginRequest;

import java.util.Arrays;

public class ChessClient {
    private ServerFacade server;
    private int port;
    private AuthData authData;
    // in petShop, it has here a notif handler and a websocket facade
    private Status status = Status.LOGGEDOUT;
    private Gson serializer = new Gson();
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
                    return "register";
                }
                case "login" -> {
                    return "login";
                }
                case "logout" -> {
                    return logout();
                }
                case "list" -> {
                    return list();
                }
                case "create" -> {
                    return "create";
                }
                case "join" -> {
                    return "FIXME SWEET SUMMER CHILD";
                }
                case "observe" -> {
                    return "FIXME ALSO";
                }
                case "help" -> {
                    return help();
                }
            }
            //we can either check here that the state is correct, or do it within the function itself
            server.login(new LoginRequest("bogus", "bogus"));
        } catch (ResponseException ex) {
            //fixme
        }
        return null;
    }

    private String register(String[] params) {
        if(params.length > 1) {
            //fixme
        }
        return null;
    }

    private String login() {
        //you'll need to set authData to the returned authData
        status = Status.LOGGEDINIDLE;
        return null;
    }

    private String logout() throws ResponseException {
        assertLoggedIn();
        server.logout(authData.authToken());
        status = Status.LOGGEDOUT;
        return String.format("Successfully logged out user %s", authData.username());
    }

    private String list() throws ResponseException {
        assertLoggedIn();
        var games = server.listGames(authData.authToken());
        var buffer = new StringBuilder();
        int gameNumber = 0;
        for(var game : games.entrySet()) {
            gameNumber += 1;
            var value = game.getValue();
            buffer.append(String.format("ID: %d\t|\tName: %s\t|\tWhite: %s\t|\tBlack: %s%n", gameNumber, value.gameName(),
                    value.whiteUsername(), value.blackUsername()));
        }
        return buffer.toString();
    }

    private String create() {

        return null;
    }

    private void join() {
        //fixme
    }

    private void observe() {
        //fixme
    }

    private String help() {
        if(status == Status.LOGGEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - to exit the program
                    help - to list possible commands
                    """;
        } else if (status == Status.LOGGEDINIDLE) {
            return """
                    create <NAME> - to create a game
                    list - to list all games
                    join <ID> [WHITE|BLACK] - to join a game
                    observe <ID> - to observe a game
                    logout - to logout of the program
                    quit - to exit the program
                    help - to list possible commands
                    """;
        } else {
            return """
                    don't worry about it for now ;')
                    """;
        }
    }

    private void assertLoggedIn() throws ResponseException {
        if(status == Status.LOGGEDOUT) {
            throw new ResponseException(400, "Error: Bad request (must log in)");
        }
    }
}
