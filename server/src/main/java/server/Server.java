package server;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.internal.LinkedTreeMap;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.requests.JoinGameRequest;
import server.requests.LoginRequest;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.HashMap;
import java.util.Map;

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


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //fixme these throw exceptions in the example so probably I should do that
    private Object registerUser(Request req, Response res) {
        AuthData result;
        try {
            var newUser = serializer.fromJson(req.body(), UserData.class);
            result = userService.register(newUser);
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
        return serializer.toJson(result);
    }

    private Object loginUser(Request req, Response res) {
        AuthData result;
        try {
            var loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
//        System.out.println("Successful creation of login request item");
            result = userService.login(loginRequest.username(), loginRequest.password());
//        System.out.println("successful return from login");

        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
        return serializer.toJson(result);
    }

    private Object logoutUser(Request req, Response res) {
        try {
            var logoutRequest = serializer.fromJson(req.headers("authorization"), String.class);
            userService.logout(logoutRequest);
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
        return serializer.toJson(new JsonNull());
    }

    private Object listGames(Request req, Response res) {
        Map<Integer, GameData> result;
        try {
            var listGamesRequest = serializer.fromJson(req.headers("authorization"), String.class);
            result = gameService.listGames(listGamesRequest);
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
        return serializer.toJson(new GameListRecord(result.values()));
    }

    private Object createGame(Request req, Response res) {
        GameData result;
        try {
            var authToken = serializer.fromJson(req.headers("authorization"), String.class);
            var requestedName = serializer.fromJson(req.body(), LinkedTreeMap.class).get("gameName");
            result = gameService.createGame(requestedName.toString(), authToken);
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }

        return serializer.toJson(result);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        try {
            var authToken = req.headers("authorization");
            //var authToken = serializer.fromJson(req.headers("Authorization"), String.class);
            var joinGameRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
            gameService.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), authToken);
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
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
