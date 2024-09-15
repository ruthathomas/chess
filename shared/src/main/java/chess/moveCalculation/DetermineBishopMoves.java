package chess.moveCalculation;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetermineBishopMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        return getValidBishopMoves(board, myPosition);
    }

    private ArrayList<ChessMove> getValidBishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        //FIXME: this MUST be refactored, with some functions extracted, but should do for now;
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        // Add available moves to the upper left
        while(r < 8 && c > 1) {
            r++;
            c--;
            ChessPosition endPos = new ChessPosition(r, c);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == piece.getTeamColor()) {
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
        c = myPosition.getColumn();
        // Add available moves to the upper right
        while(r < 8 && c < 8) {
            r++;
            c++;
            ChessPosition endPos = new ChessPosition(r, c);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == piece.getTeamColor()) {
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
        c = myPosition.getColumn();
        // Add available moves to the lower left
        while(r > 1 && c > 1) {
            r--;
            c--;
            ChessPosition endPos = new ChessPosition(r, c);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == piece.getTeamColor()) {
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
        c = myPosition.getColumn();
        // Add available moves to the lower right
        while(r > 1 && c < 8) {
            r--;
            c++;
            ChessPosition endPos = new ChessPosition(r, c);
            if(board.getPiece(endPos) != null) {
                // Player's team is in the way; pos and any tiles beyond may not be accessed
                if(board.getPiece(endPos).getTeamColor() == piece.getTeamColor()) {
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
