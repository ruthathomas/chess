package chess.moveCalculation;

import chess.*;

import java.util.ArrayList;

public class DetermineKingMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();;
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        validMoves.add(validateKingMove(row, col, 1, -1, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, 1, 0, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, 1, 1, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, 0, -1, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, 0, 1, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, -1, -1, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, -1, 0, board, myPiece, myPosition));
        validMoves.add(validateKingMove(row, col, -1, 1, board, myPiece, myPosition));
        for(int i = validMoves.size() - 1; i >= 0; i--) {
            if(validMoves.get(i) == null) {
                validMoves.remove(i);
            }
        }
        return validMoves;
    }

    private ChessMove validateKingMove(int row, int col, int rowModify, int colModify, ChessBoard board, ChessPiece myPiece, ChessPosition myPosition) {
        int newRow = row + rowModify;
        int newCol = col + colModify;
        if(newRow > 0 && newRow < 9) {
            if(newCol > 0 && newCol < 9) {
                try {
                    ChessGame.TeamColor color = board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor();
                    if(color == myPiece.getTeamColor()) {
                        // lambda; the position is blocked by a piece of the same team
                    } else {
                        // the blocking piece may be captured
                        return new ChessMove(myPosition, new ChessPosition(newRow, newCol), null);
                    }
                } catch (Exception e) {
                    // the position is unblocked
                    return new ChessMove(myPosition, new ChessPosition(newRow, newCol), null);
                }
            }
        }
        return null;
    }
}
