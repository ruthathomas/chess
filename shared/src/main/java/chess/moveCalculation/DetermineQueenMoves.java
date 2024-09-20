package chess.moveCalculation;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class DetermineQueenMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        return null;
    }

    private ChessMove validateQueenMove() {
        return null;
    }
}
