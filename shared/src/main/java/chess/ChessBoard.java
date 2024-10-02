package chess;

import java.sql.Array;
import java.util.ArrayList;
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
//        if(!piece.isHasMoved()) {
//            piece.moved();
//        }
    }

    public void movePiece(ChessPosition currentPosition, ChessPosition newPosition, ChessPiece piece) {
        //FIXME: make a function to move a piece here >:(

        // It isn't movePiece's job to check if a move is allowed or not; it just moves.
        // However, it will check if the tile it's moving to is occupied, and free space
        if(isOccupied(newPosition)) {
            //FIXME: this will probably need fixing, but for now is as simple as saying BYE to the old piece
            // this might cause memory leaks in some way. improve if possible
            tiles[newPosition.getRow() - 1][newPosition.getColumn() - 1] = null;
        }
        tiles[newPosition.getRow() - 1][newPosition.getColumn() - 1] = piece;
        tiles[currentPosition.getRow() - 1][currentPosition.getColumn() - 1] = null;
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
     * Searches for a chess piece on the chessboard
     *
     * @param piece The piece to locate
     * @return Either the position of the found piece, or null if the piece is not found
     */
    public ArrayList<ChessPosition> findPiece(ChessPiece.PieceType piece, ChessGame.TeamColor color) {
        //THIS DOESN'T WORK AND HAS TO BE FIXED
        ArrayList<ChessPosition> piecePositions = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j] == null) {
                    continue;
                }
                if(tiles[i][j].getPieceType() == piece && tiles[i][j].getTeamColor() == color) {
                    piecePositions.add(new ChessPosition(i+1, j+1));
                    // if king, return
                    if(piece == ChessPiece.PieceType.KING) {
                        return piecePositions;
                    }
                }
            }
        }
        return piecePositions;
    }

    /**
     *
     * @param team
     * @return an ArrayList of ChessPieces
     */
    public ArrayList<ChessPiece> getTeamPieces(ChessGame.TeamColor team) {
        ArrayList<ChessPiece> teamPieces = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j] != null && tiles[i][j].getTeamColor() == team) {
                    teamPieces.add(tiles[i][j]);
                }
            }
        }
        return teamPieces;
    }

    /**
     * Checks if a position on the board is occupied
     *
     * @param position The position to check occupation on
     * @return Whether the tile is occupied or not
     */
    public boolean isOccupied(ChessPosition position) {
        if(getPiece(position) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        int firstRow = 0;
        int secondRow = 0;
        for(var color : ChessGame.TeamColor.values()) {
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
