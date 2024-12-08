package chess.movecalculation;

import chess.*;

import java.util.ArrayList;

public class DetermineBishopMoves implements DeterminePieceMoves {

    //HEY: refactor completely; this is incredibly long and inefficient

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        //HEY: this MUST be refactored, with some functions extracted, but should do for now;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = myPiece.getTeamColor();
        // Add available moves to the upper left
        while(row < 8 && col > 1) {
            row++;
            col--;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                if(board.getPiece(endPos).getTeamColor() == myColor) {
                    // Piece of same color prevents movement
                    break;
                } else {
                    // Opponent's piece is in the way; further tiles blocked
                    validMoves.add(new ChessMove(myPosition, endPos, null));
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, endPos, null));
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // Add available moves to the upper right
        while(row < 8 && col < 8) {
            row++;
            col++;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                if(board.getPiece(endPos).getTeamColor() != myColor) {
                    // Opponent's piece is in the way; capture allowed, but no further movement
                    validMoves.add(new ChessMove(myPosition, endPos, null));
                    break;
                } else {
                    // Player's team is in the way; movement prevented
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, endPos, null));
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // Add available moves to the lower left
        while(row > 1 && col > 1) {
            row--;
            col--;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == myColor) {
                    break;
                    // Opponent's piece is in the way; pos is added, but tiles beyond may be accessed
                } else {
                    validMoves.add(new ChessMove(myPosition, endPos, null));
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, endPos, null));
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // Add available moves to the lower right
        while(row > 1 && col < 8) {
            row--;
            col++;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Piece of same color prevents movement
                if(board.getPiece(endPos).getTeamColor() == myColor) {
                    break;
                    // Opponent's piece is in the way; further tiles blocked
                } else {
                    validMoves.add(new ChessMove(myPosition, endPos, null));
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, endPos, null));
        }
        return validMoves;
    }
}
