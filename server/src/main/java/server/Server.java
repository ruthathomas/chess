package server;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.internal.LinkedTreeMap;
import dataaccess.*;
import exceptionhandling.ResponseException;
import model.*;
import records.*;
import requests.*;
import service.*;
import spark.*;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {

    // This feels more coupled than I would like it to be (server layer shouldn't
    // know about data layer). I want to come back later and rework, if possible.
    private DataAccessInterface dataAccess = new MemoryDataAccess();
    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private WebSocketHandler webSocketHandler;
    private Gson serializer = new Gson();

    public Server(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
        authService = new AuthService(dataAccess);
        webSocketHandler = new WebSocketHandler();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }

    public Server() {
        try {
            dataAccess = new SQLDataAccess();
            authService = new AuthService(dataAccess);
            gameService = new GameService(dataAccess);
            userService = new UserService(dataAccess);
        } catch (Exception e) {
            System.out.println(String.format("Failed to start up server with database memory: %s", e.getMessage()));
            System.out.println("Processing will continue using local data...");
        }
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //FIXME consider places where you might need websocket stuff here??

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
            result = userService.login(loginRequest.username(), loginRequest.password());
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

    private Object joinGame(Request req, Response res) {
        try {
            var authToken = req.headers("authorization");
            //HEY, LISTEN: the below code gave an error when used here, but nowhere else; investigate
            //var authToken = serializer.fromJson(req.headers("Authorization"), String.class);
            var joinGameRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
            gameService.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), authToken);
            //
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
        return serializer.toJson(new JsonNull());
    }

    private Object clear(Request req, Response res) {
        try {
            authService.clearData();
            gameService.clearData();
            userService.clearData();
            res.status(200);
            return "";
        } catch (ResponseException e) {
            res.status(e.getStatus());
            return serializer.toJson(new ExceptionFailureRecord(e.getMessage()));
        }
    }
}
