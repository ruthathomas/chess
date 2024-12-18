package chess.movecalculation;

import chess.*;

import java.util.ArrayList;

public class DetermineKnightMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        // HEY: ;^; you know what you've done
        validMoves.add(validateKnightMove(row, col, 1, 2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 1, -2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 2, 1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, 2, -1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -1, 2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -1, -2, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -2, 1, board, myPiece, myPosition));
        validMoves.add(validateKnightMove(row, col, -2, -1, board, myPiece, myPosition));
        for(int i = validMoves.size() - 1; i >= 0; i--) {
            if(validMoves.get(i) == null) {
                validMoves.remove(i);
            }
        }
        return validMoves;
    }

    public ChessMove validateKnightMove(int row, int col, int rowModify, int colModify, ChessBoard board,
                                        ChessPiece myPiece, ChessPosition myPosition) {
        // :(
        int newRow = row + rowModify;
        int newCol = col + colModify;
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessGame.TeamColor myColor = myPiece.getTeamColor();
        if(newRow > 0 && newRow < 9) {
            if(newCol > 0 && newCol < 9) {
                if(board.isTileOccupied(newPos) && board.getPiece(newPos).getTeamColor() != myColor) {
                    // pos is blocked by a capturable piece
                    return new ChessMove(myPosition, newPos, null);
                } else if (!board.isTileOccupied(newPos)) {
                    return new ChessMove(myPosition, newPos, null);
                }
            }
        }
        return null;
    }

}
