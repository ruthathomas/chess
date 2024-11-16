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
            for(int i = pieceArray.length - 1; i >= 0; i--) {
                var squareInfo = buildBoardSquare(isLightSquare, pieceArray[i], row, -1, cell);
                boardString += squareInfo[0];
                if(squareInfo[1] == "change_row") {
                    row -=1;
                }
                if(squareInfo[2] == "change_color") {
                    isLightSquare = !isLightSquare;
                }
                cell += 1;
            }
            boardString += buildVerticalBorder(color);

        } else {
            // pieceArray comes in the correct order for black
            row = 1;
            boardString += buildVerticalBorder(color);
            boardString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT;
            for(int i = 0; i < pieceArray.length; i++) {
                var squareInfo = buildBoardSquare(isLightSquare, pieceArray[i], row, 1, cell);
                boardString += squareInfo[0];
                if(squareInfo[1] == "change_row") {
                    row +=1;
                }
                if(squareInfo[2] == "change_color") {
                    isLightSquare = !isLightSquare;
                }
                cell += 1;
            }
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

    // direction will be either negative 1 (for white) or 1 for (black)
    private static String[] buildBoardSquare(boolean isLightSquare, String piece, int row, int direction, int cell) {
        String[] squareInformation = new String[3];
        String squareString = "";
        if(isLightSquare) {
            squareString += EscapeSequences.SET_BG_COLOR_LIGHT_BROWN;
        } else {
            squareString += EscapeSequences.SET_BG_COLOR_DARK_BROWN;
        }
        squareInformation[2] = "change_color";
        squareString += piece;
        if(cell % 8 == 0 && cell != 64) {
            squareString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT + "\n";
            row += direction;
            squareInformation[1] = "change_row";
            squareString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT;
            squareInformation[2] = "leave_color";
        } else if (cell % 8 == 0) {
            squareString += BORDER_FORMAT + " " + row + " " + RESET_FORMAT + "\n";
            squareInformation[1] = "leave_row";
        }
        squareInformation[0] = squareString;
        return squareInformation;
    }
}
