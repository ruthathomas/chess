package ui.clienthelpers;

/**
 * This class contains help string constants; they have been extracted here for the clarity and concision of the
 * ChessClient class.
 */

public class HelpStrings {
    public static final String LOGGED_OUT_HELP = """
        \u001b[1m\u001b[38;5;5m[OPTIONS]
        \t┝ register <USERNAME> <PASSWORD> <EMAIL> - \u001b[22m\u001b[38;5;242mto create an account
        \u001b[1m\u001b[38;5;5m\t┝ login <USERNAME> <PASSWORD> - \u001b[22m\u001b[38;5;242mto play chess
        \u001b[1m\u001b[38;5;5m\t┝ quit - \u001b[22m\u001b[38;5;242mto exit the program
        \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
        """;
    public static final String LOGGED_IN_HELP = """
        \u001b[1m\u001b[38;5;5m[OPTIONS]
        \t┝ create <NAME> - \u001b[22m\u001b[38;5;242mto create a game
        \u001b[1m\u001b[38;5;5m\t┝ list - \u001b[22m\u001b[38;5;242mto list all games
        \u001b[1m\u001b[38;5;5m\t┝ join <ID> [WHITE|BLACK] - \u001b[22m\u001b[38;5;242mto join a game
        \u001b[1m\u001b[38;5;5m\t┝ observe <ID> - \u001b[22m\u001b[38;5;242mto observe a game
        \u001b[1m\u001b[38;5;5m\t┝ logout - \u001b[22m\u001b[38;5;242mto logout of the program
        \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
        """;
    public static final String PLAYING_HELP = """
        \u001b[1m\u001b[38;5;5m[OPTIONS]
        \t┝ redraw - \u001b[22m\u001b[38;5;242mto redraw the chess board
        \u001b[1m\u001b[38;5;5m\t┝ leave - \u001b[22m\u001b[38;5;242mto leave the game (game will not end)
        \u001b[1m\u001b[38;5;5m\t┝ move <START> <END> <PIECE (opt)> - \u001b[22m\u001b[38;5;242mto move the piece at the
        \u001b[1m\u001b[38;5;5m\t|\t\u001b[22m\u001b[38;5;242m position START to the position END, and, if applicable,
        \u001b[1m\u001b[38;5;5m\t|\t\u001b[22m\u001b[38;5;242m promotes the moved piece to PIECE
        \u001b[1m\u001b[38;5;5m\t┝ resign - \u001b[22m\u001b[38;5;242mto forfeit the game (game will end)
        \u001b[1m\u001b[38;5;5m\t┝ highlight <POSITION> - \u001b[22m\u001b[38;5;242mto highlight the legal moves for the
        \u001b[1m\u001b[38;5;5m\t|\t\u001b[22m\u001b[38;5;242m piece at position POSITION
        \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
        """;
    public static final String OBSERVING_HELP = """
        \u001b[1m\u001b[38;5;5m[OPTIONS]
        \t┝ redraw - \u001b[22m\u001b[38;5;242mto redraw the chess board
        \u001b[1m\u001b[38;5;5m\t┝ leave - \u001b[22m\u001b[38;5;242mto leave the game
        \u001b[1m\u001b[38;5;5m\t┝ highlight <POSITION> - \u001b[22m\u001b[38;5;242mto highlight the legal moves for the
        \u001b[1m\u001b[38;5;5m\t|\t\u001b[22m\u001b[38;5;242m piece at position POSITION
        \u001b[1m\u001b[38;5;5m\t┕ help - \u001b[22m\u001b[38;5;242mto list possible commands
        """;
}
