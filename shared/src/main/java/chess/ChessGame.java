package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        // White always goes first
        teamTurn = TeamColor.WHITE;
        // Setup board for play
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> pieceCollection = piece.pieceMoves(gameBoard, startPosition);
        ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) pieceCollection;
        //can't move anywhere that causes check
        for(int i = pieceMoves.size() - 1; i >= 0; i--) {
            if(doesMoveCheck(pieceMoves.get(i))) {
                pieceMoves.remove(i);
            }
        }
        return pieceMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //FIXME this is a work in progress
        //FIXME - ensure it's YOUR TURN
        if(gameBoard.getPiece(move.getStartPosition()) == null) {
            // no piece on selected start position
            throw new InvalidMoveException();
        }
        if(gameBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        if(!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        } else {
            // the move is valid
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            // creation of these ChessPiece variables isn't strictly necessary, but improves clarity
            ChessPiece movePiece = gameBoard.getPiece(start);
            if(move.getPromotionPiece() != null) {
                // pawn must be promoted
                ChessPiece promoPiece = new ChessPiece(movePiece.getTeamColor(), move.getPromotionPiece());
                gameBoard.movePiece(start, end, promoPiece);
            } else {
                gameBoard.movePiece(start, end, movePiece);
            }
        }
        if(teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * @param move the ChessMove to investigate
     * @return a boolean indicating whether the move produces check
     */
    public boolean doesMoveCheck(ChessMove move) {
        ChessBoard tempBoard = new ChessBoard();
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if(gameBoard.isTileOccupied(new ChessPosition(i,j))) {
                    tempBoard.addPiece(new ChessPosition(i, j), gameBoard.getPiece(new ChessPosition(i, j)));
                }
            }
        }
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());
        tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), piece);
        if(piece == null) {
            return false;
        }
        if(isInCheck(piece.getTeamColor(), tempBoard)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //FIXME find a way to know where the king be??
        //FIXME THIS IS HOT TRASH
        //or where the other ones are?? >:((
        if(gameBoard.findPiece(ChessPiece.PieceType.KING, teamColor).isEmpty()) {
            return false;
        }
        ChessPosition kingPosition = gameBoard.findPiece(ChessPiece.PieceType.KING, teamColor).get(0);
        // opposing color defaults to WHITE, but is switched to black if teamColor is WHITE
        TeamColor opposingColor = TeamColor.WHITE;
        if(teamColor == TeamColor.WHITE) {
            opposingColor = TeamColor.BLACK;
        }
        // ArrayList<ChessPiece> opposingPieces = gameBoard.getTeamPieces(opposingColor);
        ArrayList<ChessMove> opposingMoves = new ArrayList<>();
        for(var type : ChessPiece.PieceType.values()) {
            for(var p : gameBoard.findPiece(type, opposingColor)) {
                var piece = gameBoard.getPiece(p);
                opposingMoves.addAll(piece.pieceMoves(gameBoard, new ChessPosition(p.getRow(), p.getColumn())));
            }
        }
        for(var move : opposingMoves) {
//            System.out.println(move.getEndPosition());
//            System.out.println(kingPosition);
//            System.out.println(move.getEndPosition() == kingPosition);
            if(move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()) { return true; }
        }
        return false;
    }

    /**
     * Overload of isInCheck for use in the doesMoveCheck function
     */
    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        if(board.findPiece(ChessPiece.PieceType.KING, teamColor).isEmpty()) {
            return false;
        }
        ChessPosition kingPosition = board.findPiece(ChessPiece.PieceType.KING, teamColor).get(0);
        // opposing color defaults to WHITE, but is switched to black if teamColor is WHITE
        TeamColor opposingColor = TeamColor.WHITE;
        if(teamColor == TeamColor.WHITE) {
            opposingColor = TeamColor.BLACK;
        }
        // ArrayList<ChessPiece> opposingPieces = gameBoard.getTeamPieces(opposingColor);
        ArrayList<ChessMove> opposingMoves = new ArrayList<>();
        for(var type : ChessPiece.PieceType.values()) {
            for(var p : board.findPiece(type, opposingColor)) {
                var piece = board.getPiece(p);
                opposingMoves.addAll(piece.pieceMoves(board, new ChessPosition(p.getRow(), p.getColumn())));
            }
        }
        for(var move : opposingMoves) {
            if(move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()) { return true; }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ArrayList<ChessPiece> teamPieces = gameBoard.getTeamPieces(teamColor);
        ArrayList<ChessMove> teamMoves = new ArrayList<>();
        for(var type : ChessPiece.PieceType.values()) {
            for(var p : gameBoard.findPiece(type, teamColor)) {
                teamMoves.addAll(validMoves(new ChessPosition(p.getRow(), p.getColumn())));
            }
        }
        for(var move : teamMoves) {
            // if a single move removes check, we can return false
            if(!doesMoveCheck(move)) { return false; }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessPiece> teamPieces = gameBoard.getTeamPieces(teamColor);
        ArrayList<ChessMove> teamMoves = new ArrayList<>();
        // stalemate occurs when you are NOT in check, but cannot move
        if(isInCheck(teamColor)) {
            return false;
        }
        for(var type : ChessPiece.PieceType.values()) {
            for(var p : gameBoard.findPiece(type, teamColor)) {
                teamMoves.addAll(validMoves(new ChessPosition(p.getRow(), p.getColumn())));
            }
        }
        return teamMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
