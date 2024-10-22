package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
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
        // Spark.post("/session", "FIXME");
        // Logout Endpoint
        // Spark.delete("/session", "FIXME");
        // List Games Endpoint
        // Spark.get("/game", "FIXME");
        // Create Game Endpoint
        // Spark.post("/game", "FIXME");
        // Join Game Endpoint
        // Spark.put("/game", "FIXME");

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
        //FIXME
        System.out.println("Exception caught; fixme");
    }
    //throw exceptions on these?

    //fixme these throw exceptions in the example so probably I should do that
    private Object registerUser(Request req, Response res) {
        var newUser = serializer.fromJson(req.body(), UserData.class);
        var result = userService.register(newUser);
        return serializer.toJson(result);
    }

    private Object loginUser(Request req, Response res) {

        return null;
    }

    private Object logoutUser(Request req, Response res) {

        return null;
    }

    private Object joinGame(Request req, Response res) {

        return null;
    }

    private Object clear(Request req, Response res) throws ResponseException {
        authService.clearData();
        gameService.clearData();
        userService.clearData();
        res.status(200);
        return "";
    }
}
