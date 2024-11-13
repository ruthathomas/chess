package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    // class in which to implement your repl loop
    private ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }
}
