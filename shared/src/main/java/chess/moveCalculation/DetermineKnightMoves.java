package chess.moveCalculation;

import chess.*;

import java.util.ArrayList;

public class DetermineKnightMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        //FIXME ;^; you know what you've done
        validMoves.add(validateKnightMove(row, col, 1, 2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 1, -2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 2, 1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 2, -1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -1, 2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -1, -2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -2, 1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -2, -1, board, myPiece, myPosition));
        for(int i = 0; i < validMoves.size(); i++) {
            if(validMoves.get(i) == null) {
                validMoves.remove(i);
            }
        }
        return validMoves;
    }

    public ChessMove validateKnightMove(int row, int col, int rowModify, int colModify, ChessBoard board, ChessPiece myPiece, ChessPosition myPosition) {
        // :(
        int newRow = row + rowModify;
        int newCol = col + colModify;
        if(newRow > 0 && newRow < 9) {
            if(newCol > 0 && newCol < 9) {
                try {
                    ChessGame.TeamColor color = board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor();
                    if(color == myPiece.getTeamColor()) {
                        // lambda; the blocking piece is of the same team
                    } else {
                        // the blocking piece may be captured
                        return new ChessMove(myPosition, new ChessPosition(newRow, newCol), null);
                    }
                } catch (Exception e) {
                    return new ChessMove(myPosition, new ChessPosition(newRow, newCol), null);
                }
            }
        }
        return null;
    }

}
