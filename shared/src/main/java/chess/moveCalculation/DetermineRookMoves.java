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
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        ChessPiece myPiece = board.getPiece(myPosition);
        // Add available moves forward
        while(r < 8) {
            r++;
            ChessPosition endPos = new ChessPosition(r, c);
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
        r = myPosition.getRow();
        // Add available moves backward
        while(r > 1) {
            r--;
            ChessPosition endPos = new ChessPosition(r, c);
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
        r = myPosition.getRow();
        // Add available moves to the left
        while(c > 1) {
            c--;
            ChessPosition endPos = new ChessPosition(r, c);
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
        c = myPosition.getColumn();
        // Add available moves to the right
        while(c < 8) {
            c++;
            ChessPosition endPos = new ChessPosition(r, c);
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
