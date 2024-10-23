package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import chess.movecalculation.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
//    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
//        hasMoved = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(type) {
            case KING -> {
                DetermineKingMoves kingMoves = new DetermineKingMoves();
                return kingMoves.getValidMoves(board, myPosition);
            }
            case QUEEN -> {
                DetermineQueenMoves queenMoves = new DetermineQueenMoves();
                return queenMoves.getValidMoves(board, myPosition);
            }
            case BISHOP -> {
                DetermineBishopMoves bishopMoves = new DetermineBishopMoves();
                return bishopMoves.getValidMoves(board, myPosition);
            }
            case KNIGHT -> {
                DetermineKnightMoves knightMoves = new DetermineKnightMoves();
                return knightMoves.getValidMoves(board, myPosition);
            }
            case ROOK -> {
                DetermineRookMoves rookMoves = new DetermineRookMoves();
                return rookMoves.getValidMoves(board, myPosition);
            }
            case PAWN -> {
                DeterminePawnMoves pawnMoves = new DeterminePawnMoves();
                return pawnMoves.getValidMoves(board, myPosition);
            }
            case null, default -> {
                return new ArrayList<>();
            }
        }
    }
}
