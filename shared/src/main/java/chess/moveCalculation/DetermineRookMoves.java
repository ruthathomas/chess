package chess.moveCalculation;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetermineRookMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        return getValidRookMoves(board, myPosition);
    }

    private ArrayList<ChessMove> getValidRookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        // Add available moves forward
        while(row < 8) {
            row++;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == myPiece.getTeamColor()) {
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
        // Add available moves backward
        while(row > 1) {
            row--;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == myPiece.getTeamColor()) {
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
        // Add available moves to the left
        while(col > 1) {
            col--;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == myPiece.getTeamColor()) {
                    break;
                    // Opponent's piece is in the way; pos is added, but tiles beyond may be accessed
                } else {
                    validMoves.add(new ChessMove(myPosition, endPos, null));
                    break;
                }
            }
            validMoves.add(new ChessMove(myPosition, endPos, null));
        }
        col = myPosition.getColumn();
        // Add available moves to the right
        while(col < 8) {
            col++;
            ChessPosition endPos = new ChessPosition(row, col);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == myPiece.getTeamColor()) {
                    break;
                    // Opponent's piece is in the way; pos is added, but tiles beyond may be accessed
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
