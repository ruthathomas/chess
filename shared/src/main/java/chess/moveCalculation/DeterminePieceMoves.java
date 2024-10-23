package chess.movecalculation;

import chess.*;
import java.util.ArrayList;

public interface DeterminePieceMoves {

    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition);
    //HEY: I don't know anything about interfaces yet. but I will
    //HEY: Determine/add the methods for the subclasses to implement
}
