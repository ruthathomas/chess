import chess.*;
import dataaccess.DataAccessInterface;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // this class has been modeled off of the provided 'PetShop' example

        var port = 8080;
        if (args.length >=1) {
            // a specific port has been provided
            port = Integer.parseInt(args[0]);
        }

        DataAccessInterface dataAccess = new MemoryDataAccess();
        if(args.length >= 2 && args[1] == "sql") {
            // an argument has been passed indicating that memory access should use the database
            dataAccess = new SQLDataAccess();
        }
        //all sorts of stuff
        var server = new Server();
        server.run(8080);




    }
}