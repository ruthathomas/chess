package chess.movecalculation;

import chess.*;

import java.util.ArrayList;

public class DetermineQueenMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        // Advance to the upper left
        for(var move : validateQueenMove(row, col, 1, -1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance forward
        for(var move : validateQueenMove(row, col, 1, 0, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance to the upper right
        for(var move : validateQueenMove(row, col, 1, 1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance to the left
        for(var move : validateQueenMove(row, col, 0, -1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance to the right
        for(var move : validateQueenMove(row, col, 0, 1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance to the lower left
        for(var move : validateQueenMove(row, col, -1, -1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance backwards (that seems oxymoronic)
        for(var move : validateQueenMove(row, col, -1, 0, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        // Advance to the lower right
        for(var move : validateQueenMove(row, col, -1, 1, board, myPiece, myPosition)) {
            if(move != null) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private ArrayList<ChessMove> validateQueenMove(int row, int col, int verticalDirection, int horizontalDirection,
                                                   ChessBoard board, ChessPiece myPiece, ChessPosition myPosition) {
        // for 'Direction' variables, 1 indicates up or right, 0 indicates no change, and -1 indicates down or left
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int startRow = row + verticalDirection;
        int startCol = col + horizontalDirection;
        while(startRow > 0 && startRow < 9 && startCol > 0 && startCol < 9) {
            ChessPosition newPosition = new ChessPosition(startRow, startCol);
            if(board.isTileOccupied(newPosition)) {
                if(board.getPiece(newPosition).getTeamColor() == myPiece.getTeamColor()) {
                    // the position is blocked by a piece of the same color, and may not be passed
                    break;
                } else {
                    // the position is blocked by an enemy; may be captured, but not passed
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
                startRow += verticalDirection;
                startCol += horizontalDirection;
            }
        }
        return validMoves;
    }
}
