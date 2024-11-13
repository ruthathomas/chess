package ui;

import com.google.gson.Gson;
import model.UserData;
import server.ResponseException;

import java.net.*;

public class ServerFacade {

    private String serverUrl = "http://localhost:";

    public ServerFacade(int port) {
        serverUrl += port;
    }

    private void register(UserData user) {
        try {
            this.testing("/user", "POST", user);
        } catch (ResponseException ex) {
            //lol
        }

    }

    /**
     *
     * @param path - the request path for the url
     * @param method - the http method to be used
     * @throws ResponseException
     */
    private void testing(String path, String method, Object request) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            // set up http connection
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(method);
            // and then for some reason you have to turn on output?
            httpConn.setDoOutput(true);
            //fixme continue
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // for handling sending/receiving HTTP requests; calls server

    // register, join, etc.

    // PRELOGIN UI

    // POSTLOGIN UI

    // GAMEPLAY UI

    // DO NOT DISPLAY JSON, AUTHTOKENS, GAMEIDS, HTTP STATUS CODES, EXCEPTION STACK TRACES
    // DO NOT LET YOUR CLIENT CRASH; CATCH ALL EXCEPTIONS (too few/many arguments, bad arguments, rejected arguments)
    // IF you wanna use the unicode chess cars, go Settings > Time and Lang > Lang and Region > Admin Lang Settings >
    //  Admin tab > Change System Locale > check using UTF-8 (if there's been an issue; will require a reboot)
    // If unicode chars too wide, use \u2003 for spaces, bc it's slightly wider
}
