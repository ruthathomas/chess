package ui;

import chess.ChessGame;

public class BoardBuilder {

    public static String buildBoard(String[] pieceArray, ChessGame.TeamColor color) {
        String boardString = "";
        if(color == ChessGame.TeamColor.WHITE) {
            // pieceArray comes backwards for white
            boardString += buildVerticalBorder(color);
            //fixme add board in here
            boardString += buildVerticalBorder(color);

        } else {
            // pieceArray comes in the correct order for black
            boardString += buildVerticalBorder(color);
            //fixme add board in here
            boardString += buildVerticalBorder(color);
        }
        return boardString;
    }

    private static String buildVerticalBorder(ChessGame.TeamColor color) {
        String borderString = EscapeSequences.SET_BG_COLOR_LIGHT_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK +
                EscapeSequences.SET_TEXT_BOLD;
        if(color == ChessGame.TeamColor.WHITE) {
            return borderString + EscapeSequences.EMPTY + " a  b  c  d  e  f  g  h " + EscapeSequences.EMPTY +
                    EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT + "\n";
        } else {
            return borderString + EscapeSequences.EMPTY + " h  g  f  e  d  c  b  a " + EscapeSequences.EMPTY +
                    EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT + "\n";
        }
    }
}
