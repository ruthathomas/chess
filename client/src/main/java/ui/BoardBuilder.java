package ui;

import chess.ChessGame;

public class BoardBuilder {

    private static final String BORDER_FORMAT = EscapeSequences.SET_BG_COLOR_LIGHT_BLUE +
            EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_TEXT_BOLD;
    private static final String RESET_FORMAT = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT;

    public static String buildBoard(String[] pieceArray, ChessGame.TeamColor color) {
        String boardString = "";
        int row;
        int cell = 1;
        boolean isLightSquare = true;
        if(color == ChessGame.TeamColor.WHITE) {
            // pieceArray comes backwards for white
            row = 8;
            boardString += buildVerticalBorder(color);
            boardString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT;
            //fixme add board in here
            for(int i = pieceArray.length - 1; i >= 0; i--) {
                if(isLightSquare) {
                    boardString += EscapeSequences.SET_BG_COLOR_LIGHT_BROWN;
                } else {
                    boardString += EscapeSequences.SET_BG_COLOR_DARK_BROWN;
                }
                isLightSquare = !isLightSquare;
                boardString += pieceArray[i];
                if(cell % 8 == 0 && cell != 64) {
                    boardString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT + "\n";
                    row -= 1;
                    boardString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT;
                    isLightSquare = !isLightSquare;
                } else if (cell % 8 == 0) {
                    boardString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT + "\n";
                }
                cell += 1;
            }
            boardString += buildVerticalBorder(color);

        } else {
            // pieceArray comes in the correct order for black
            row = 1;
            boardString += buildVerticalBorder(color);
            //fixme add board in here
            boardString += buildVerticalBorder(color);
        }
        return boardString;
    }

    private static String buildVerticalBorder(ChessGame.TeamColor color) {
        String borderString = BORDER_FORMAT;
        if(color == ChessGame.TeamColor.WHITE) {
            return borderString + EscapeSequences.EMPTY + "ａ  ｂ  ｃ  ｄ  ｅ  ｆ  ｇ  ｈ" + EscapeSequences.EMPTY +
                    RESET_FORMAT + "\n";
        } else {
            return borderString + EscapeSequences.EMPTY + "ｈ  ｇ  ｆ  ｅ  ｄ  ｃ  ｂ  ａ" + EscapeSequences.EMPTY +
                    RESET_FORMAT + "\n";
        }
    }
}
