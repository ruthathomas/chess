package ui;

import static ui.clienthelpers.BoardBuilder.buildBoard;
import static ui.clienthelpers.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.clienthelpers.EscapeSequences;
import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    // class in which to implement your repl loop
    private ChessClient client;

    public Repl(int port) {
        client = new ChessClient(port, this);
    }

    public void run() {
        //sample
        System.out.println("Sign in to start.");
        System.out.print("\t" + client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equalsIgnoreCase("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            try {
                result = client.evaluateInput(input);
                System.out.print("\t" + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + result);
            } catch (Exception e) {
                var msg = "Error : " + e.getMessage();
                System.out.print("\t" + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String status = client.getStatus();
        String promptString = RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY;
        if(status.equals("LOGGEDOUT")) {
            status = "LOGGED OUT";
        } else if(status.equals("LOGGEDINIDLE")) {
            status = "LOGGED IN";
        } else if(status.equals("LOGGEDINPLAYING")) {
            status = "PLAYING";
        } else if(status.equals("LOGGEDINOBSERVING")) {
            status = "OBSERVING";
        }
        System.out.print(promptString + "\u001b[3m\n["+ status +"] >>> ");
    }

    public void notify(ServerMessage notification) {
        String notifString = "\n\t" + SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_ITALIC;
        switch(notification.getServerMessageType()) {
            case LOAD_GAME -> {
                // send current game state; will redraw chess board
                notifString += "LOADING GAME..." + RESET_TEXT_ITALIC + SET_TEXT_COLOR_WHITE;
                //fixme something about in check?? idk what it wants tbh
                notifString += notification.getMessage();
                //FIXME do something here
            }
            case ERROR -> {
                // invalid command; should only be seen by user who caused error??
                notifString += "ERROR: " + RESET_TEXT_ITALIC + SET_TEXT_COLOR_WHITE;
                notifString += notification.getMessage();
            }
            case NOTIFICATION -> {
                // another player made an action
                notifString += "NOTIFICATION: " + RESET_TEXT_ITALIC + SET_TEXT_COLOR_WHITE;
                notifString += notification.getMessage();
            }
        }
        // >:( need to fix what this thing takes
        notifString += RESET_TEXT_COLOR;
        System.out.println(notifString);
        // make the redraw work :((
        if(notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            String tryMe = client.evaluateInput("redraw");
            //??? didn't work?? of course it didn't but
            System.out.println(tryMe);
        }
        printPrompt();
    }

}
