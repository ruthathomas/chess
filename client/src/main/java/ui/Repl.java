package ui;

import java.util.Scanner;

public class Repl {
    // class in which to implement your repl loop
    private ChessClient client;

    public Repl(int port) {
        client = new ChessClient(port);
    }

    public void run() {
        //sample
        System.out.println("Welcome to Chess :') Sign into start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equalsIgnoreCase("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            try {
                result = client.evaluateInput(input);
                System.out.print(result);
            } catch (Exception e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String status = client.getStatus();
        if(status.equals("LOGGEDOUT")) {
            status = "LOGGED OUT";
        } else if(status.equals("LOGGEDINIDLE")) {
            status = "LOGGED IN";
        } else if(status.equals("LOGGEDINPLAYING")) {
            status = "PLAYING";
        } else if(status.equals("LOGGEDINOBSERVING")) {
            status = "OBSERVING";
        }
        System.out.print("\n["+ status +"] >>> ");
    }
}
