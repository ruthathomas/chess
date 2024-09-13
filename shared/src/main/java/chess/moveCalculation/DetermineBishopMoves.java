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

    private ArrayList<ChessMove> getValidBishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        //FIXME: this feels incredibly inefficient, but should do for now;
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        while(r < 8 && c > 1) {
            r++;
            c--;
            validMoves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        }
        r = myPosition.getRow();
        c = myPosition.getColumn();
        while(r < 8 && c < 8) {
            r++;
            c++;
            validMoves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        }
        r = myPosition.getRow();
        c = myPosition.getColumn();
        while(r > 1 && c > 1) {
            r--;
            c--;
            validMoves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        }
        r = myPosition.getRow();
        c = myPosition.getColumn();
        while(r > 1 && c < 8) {
            r--;
            c++;
            validMoves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        }
        return validMoves;
    }
}
