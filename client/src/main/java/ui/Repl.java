package ui;

import static ui.clienthelpers.EscapeSequences.*;

import websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
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
        String notifString = "\n\t" + SET_TEXT_COLOR_WHITE;
        switch(notification.getServerMessageType()) {
            case LOAD_GAME -> {
                // send current game state; will redraw chess board
                notifString += "LOADING GAME: ";
                //FIXME do something here
            }
            case ERROR -> {
                // invalid command
                notifString += "ERROR: ";
                notifString += ((ErrorMessage) notification).getErrorMessage();
            }
            case NOTIFICATION -> {
                // another player made an action
                notifString += "NOTIFICATION: ";
                notifString += ((NotificationMessage) notification).getMessage();
            }
        }
        // >:( need to fix what this thing takes
        notifString += RESET_TEXT_COLOR;
        System.out.println(notifString);
        printPrompt();
    }
}
