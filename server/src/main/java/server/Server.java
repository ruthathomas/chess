package server;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.internal.LinkedTreeMap;
import dataaccess.MemoryDataAccess;
import model.UserData;
import server.requests.JoinGameRequest;
import server.requests.LoginRequest;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    //FIXME this feels more coupled than I would like it to be. Server layer shouldn't
    // know about Data Layer. I want to come back later and rework, if possible.
    private MemoryDataAccess localMemory = new MemoryDataAccess();
    private AuthService authService = new AuthService(localMemory);
    private GameService gameService = new GameService(localMemory);
    private UserService userService = new UserService(localMemory);
    private Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Clear Endpoint
        Spark.delete("/db", this::clear);
        // Register User Endpoint
        Spark.post("/user", this::registerUser);
        // Login Endpoint
        Spark.post("/session", this::loginUser);
        // Logout Endpoint
        Spark.delete("/session", this::logoutUser);
        // List Games Endpoint
        Spark.get("/game", this::listGames);
        // Create Game Endpoint
        Spark.post("/game", this::createGame);
        // Join Game Endpoint
        Spark.put("/game", this::joinGame);
        //Exception Handler
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.GetStatus());
        //Stubbing
        System.out.println(ex.GetStatus());
        System.out.println("Exception caught; fixme");
    }
    //throw exceptions on these?

    //fixme these throw exceptions in the example so probably I should do that
    private Object registerUser(Request req, Response res) throws ResponseException {
        var newUser = serializer.fromJson(req.body(), UserData.class);
        var result = userService.register(newUser);
        return serializer.toJson(result);
    }

    private Object loginUser(Request req, Response res) throws ResponseException {
        try {
            var loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
//        System.out.println("Successful creation of login request item");
            var result = userService.login(loginRequest.username(), loginRequest.password());
//        System.out.println("successful return from login");
            return serializer.toJson(result);
        } catch (ResponseException e) {
            throw e;
        }
    }

    private Object logoutUser(Request req, Response res) throws ResponseException {
        var logoutRequest = serializer.fromJson(req.headers("authorization"), String.class);
        userService.logout(logoutRequest);
        return serializer.toJson(new JsonNull());
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        var listGamesRequest = serializer.fromJson(req.headers("authorization"), String.class);
        var result = gameService.listGames(listGamesRequest);
        return serializer.toJson(result);
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        var authToken = serializer.fromJson(req.headers("authorization"), String.class);
        var requestedName = serializer.fromJson(req.body(), LinkedTreeMap.class).get("gameName");
        var result = gameService.createGame(requestedName.toString(), authToken);
        return serializer.toJson(result);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        var authToken = serializer.fromJson(req.headers("authorization"), String.class);
        var joinGameRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
        gameService.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), authToken);
        return serializer.toJson(new JsonNull());
    }

    private Object clear(Request req, Response res) throws ResponseException {
        authService.clearData();
        gameService.clearData();
        userService.clearData();
        res.status(200);
        return "";
    }
}
