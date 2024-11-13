package ui;

import server.ResponseException;
import server.requests.LoginRequest;

public class ChessClient {
    private ServerFacade server;
    private int port;
    //fixme I'm working on it

    public ChessClient(int port) {
        this.port = port;
        server = new ServerFacade(port);
        //probably you need to make a notification handler ;')
    }

    public String evaluateInput(String input) {
        try {
            server.login(new LoginRequest("bogus", "bogus"));
        } catch (ResponseException ex) {
            //fixme
        }
        return null;
    }
}
