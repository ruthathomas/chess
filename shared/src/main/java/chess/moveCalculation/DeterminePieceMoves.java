package chess.moveCalculation;

import chess.*;
import java.util.ArrayList;

public interface DeterminePieceMoves {

    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition);
    //FIXME: I don't know anything about interfaces yet. but I will
    //FIXME: Determine/add the methods for the subclasses to implement
}
