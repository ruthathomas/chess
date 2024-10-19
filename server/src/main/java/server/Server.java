package server;

import spark.*;

public class Server {

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
