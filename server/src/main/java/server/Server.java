package server;

import dataaccess.MemoryDataAccess;
import org.eclipse.jetty.server.Authentication;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private MemoryDataAccess localMemory = new MemoryDataAccess();
    private AuthService authService = new AuthService(localMemory);
    private GameService gameService = new GameService(localMemory);
    private UserService userService = new UserService(localMemory);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Clear Endpoint
        // Spark.delete("/db", "FIXME");
        // Register User Endpoint
        // Spark.post("/user", "FIXME");
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
}
