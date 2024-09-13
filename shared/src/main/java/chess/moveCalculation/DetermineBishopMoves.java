package chess.moveCalculation;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetermineBishopMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        return getValidBishopMoves(board, myPosition);
    }
    //FIXME: >:) implement the methods from DeterminePieceMoves here

    private ArrayList<ChessMove> getValidBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return null;
    }
}
