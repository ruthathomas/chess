package ui;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import model.*;
import server.ResponseException;
import server.requests.*;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {

    private String serverUrl = "http://localhost:";

    public ServerFacade(int port) {
        serverUrl += port;
    }

    public AuthData register(UserData user) throws ResponseException {
        return this.makeRequest("/user", "POST", null, user, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        return this.makeRequest("/session", "POST", null, loginRequest, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        this.makeRequest("/session", "DELETE", authToken, null,null);
    }

    public Map<Integer, GameData> listGames(String authToken) throws ResponseException {
        // so this is a concern...we'll assume we get the correct type of map back until it becomes a problem
        return this.makeRequest("/game", "GET", authToken, null, Map.class);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        LinkedTreeMap<String, String> treeMap = new LinkedTreeMap<>();
        treeMap.put("gameName", gameName);
        return this.makeRequest("/game", "POST", authToken, treeMap, GameData.class);
    }

    public void joinGame() {
        //FIXME
        //not sure what to do about this one? need to be able to join as an observer or player
    }

    // he is dangerous but I need him for testing
    public void clear() throws ResponseException {
        this.makeRequest("/db", "DELETE", null, null, null);
    }

    /**
     * Make the given request to the server via HttpURLConnection
     * @param path a String; the request path for the url
     * @param method a String; the http method to be used
     * @param authToken a String; the authentication token for authorization; not required in all cases (may be null)
     * @param request an Object; the information to be used in the request body
     * @param responseClass a Class; the class which is the data type which the request expects as a response
     * @return an object of the same type as responseClass
     * @throws ResponseException
     */
    private <T> T makeRequest(String path, String method, String authToken, Object request, Class<T> responseClass)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            // set up http connection
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(method);
            // add the header
            if(authToken != null) {
                httpConn.setRequestProperty("authorization", authToken);
            }
            // and then for some reason you have to turn on output?
            httpConn.setDoOutput(true);

            writeRequestBody(request, httpConn);
            httpConn.connect();
            var status = httpConn.getResponseCode();
            if(status != 200) {
                throw new ResponseException(status, "ERROR " + status);
            }
            return readResponseBody(httpConn, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void writeRequestBody(Object request, HttpURLConnection httpConn) throws IOException {
        if(request != null) {
            // let the HttpUrlConnection know that the request is in json
            httpConn.addRequestProperty("Content-Type", "application/json");
            String requestString = new Gson().toJson(request);
            // try to write the request to the HttpUrlConnection object's output stream
            try(OutputStream requestBody = httpConn.getOutputStream()) {
                requestBody.write(requestString.getBytes());
            }
        }
        // Should it do anything if the request is null?
    }

    private <T> T readResponseBody(HttpURLConnection httpConn, Class<T> responseClass) throws IOException {
        T response = null;
        // I don't understand this part; why if it's less than 0?? research
        if(httpConn.getContentLength() < 0) {
            // try to read the response from the HttpUrlConnection object's input stream
            try(InputStream responseBody = httpConn.getInputStream()) {
                InputStreamReader streamReader = new InputStreamReader(responseBody);
                if(responseClass != null) {
                    // read the json from the streamReader object; create an object of responseClass
                    response = new Gson().fromJson(streamReader, responseClass);
                }
            }
        }
        return response;
    }

    // for handling sending/receiving HTTP requests; calls server

    // GAMEPLAY UI

    // DO NOT DISPLAY JSON, AUTHTOKENS, GAMEIDS, HTTP STATUS CODES, EXCEPTION STACK TRACES
    // DO NOT LET YOUR CLIENT CRASH; CATCH ALL EXCEPTIONS (too few/many arguments, bad arguments, rejected arguments)
    // IF you wanna use the unicode chess cars, go Settings > Time and Lang > Lang and Region > Admin Lang Settings >
    //  Admin tab > Change System Locale > check using UTF-8 (if there's been an issue; will require a reboot)
    // If unicode chars too wide, use \u2003 for spaces, bc it's slightly wider
}
