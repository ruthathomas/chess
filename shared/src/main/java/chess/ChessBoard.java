package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] tiles = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "tiles=" + Arrays.toString(tiles) +
                '}';
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        tiles[position.getRow() - 1][position.getColumn() - 1] = piece;
        if(!piece.isHasMoved()) {
            piece.moved();
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return tiles[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(var color : ChessGame.TeamColor.values()) {
            int firstRow = 0;
            int secondRow = 0;
            if(color == ChessGame.TeamColor.WHITE) {
                firstRow = 1;
                secondRow = 2;
            } else {
                firstRow = 8;
                secondRow = 7;
            }
            // rooks
            addPiece(new ChessPosition(firstRow, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            addPiece(new ChessPosition(firstRow, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            // knights
            addPiece(new ChessPosition(firstRow, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(firstRow, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            // bishops
            addPiece(new ChessPosition(firstRow, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(firstRow, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            // queen
            addPiece(new ChessPosition(firstRow, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            // king
            addPiece(new ChessPosition(firstRow, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
            // pawns
            for(int i = 1; i < 9; i++) {
                addPiece(new ChessPosition(secondRow, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
            }
        }
    }

    /**
     * Returns whether a tile is occupied or not
     *
     * @param position The position to check
     * @return A boolean indicating occupation status
     */
    public boolean isTileOccupied(ChessPosition position) {
        // If the tile is not empty, return true; otherwise, return false
        return tiles[position.getRow() - 1][position.getColumn() - 1] != null;
    }
}
