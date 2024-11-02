import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import server.Server;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        // this class has been modeled off of the provided 'PetShop' example

        var port = 8080;
        if (args.length >=1) {
            // a specific port has been provided
            port = Integer.parseInt(args[0]);
        }

        DataAccessInterface dataAccess = new MemoryDataAccess();
        if(args.length >= 2 && args[1].equals("sql")) {
            // an argument has been passed indicating that memory access should use the database
            try {
                dataAccess = new SQLDataAccess();
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
            }
        }

        // boot up the server; services are initialized within the server
        var server = new Server(dataAccess);
        server.run(port);
        // print to console the port number and the kind of memory being used
        System.out.printf("Server running on port %d with memory type %s", Spark.port(), dataAccess.getClass());
    }
}