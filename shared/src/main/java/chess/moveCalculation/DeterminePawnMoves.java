package chess.moveCalculation;

import chess.*;

import java.util.ArrayList;

public class DeterminePawnMoves implements DeterminePieceMoves {

    @Override
    public ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = myPiece.getTeamColor();
        if(myColor == ChessGame.TeamColor.WHITE) {
            for(var move : validatePawnMove(row, col, 1, board, myPosition, myColor)) {
                if(move != null) {
                    validMoves.add(move);
                }
            }
        } else {
            for(var move : validatePawnMove(row, col, -1, board, myPosition, myColor)) {
                if(move != null) {
                    validMoves.add(move);
                }
            }
        }
        return validMoves;
    }

    public ArrayList<ChessMove> validatePawnMove(int row, int col, int direction, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        // direction is indicated by a positive or negative 1; positive for WHITE, negative for BLACK
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        if(row + direction == 1 || row + direction == 8) {
            // piece can move to promotion zone
            ChessPosition fwd = new ChessPosition(row + direction, col);
            if(!board.isTileOccupied(fwd)) {
                // forward movement is open
                for(var piece : ChessPiece.PieceType.values()) {
                    if(piece != ChessPiece.PieceType.PAWN && piece != ChessPiece.PieceType.KING) {
                        validMoves.add(new ChessMove(myPosition, fwd, piece));
                    }
                }
            }
            if(col - 1 > 0) {
                ChessPosition leftAttack = new ChessPosition(row + direction, col - 1);
                if(board.isTileOccupied(leftAttack) && board.getPiece(leftAttack).getTeamColor() != myColor) {
                    // attack to the left is possible
                    for(var piece : ChessPiece.PieceType.values()) {
                        if(piece != ChessPiece.PieceType.PAWN && piece != ChessPiece.PieceType.KING) {
                            validMoves.add(new ChessMove(myPosition, leftAttack, piece));
                        }
                    }
                }
            }
            if(col + 1 < 9) {
                ChessPosition rightAttack = new ChessPosition(row + direction, col + 1);
                if(board.isTileOccupied(rightAttack) && board.getPiece(rightAttack).getTeamColor() != myColor) {
                    // attack to the right is possible
                    for(var piece : ChessPiece.PieceType.values()) {
                        if(piece != ChessPiece.PieceType.PAWN && piece != ChessPiece.PieceType.KING) {
                            validMoves.add(new ChessMove(myPosition, rightAttack, piece));
                        }
                    }
                }
            }
        } else if(row + direction > 1 && row + direction < 8) {
            // piece can move forward, but not into promotion zone
            //FIXME >:(
            ChessPosition fwd = new ChessPosition(row + direction, col);
            if(!board.isTileOccupied(fwd)) {
                // forward movement is not blocked
                validMoves.add(new ChessMove(myPosition, fwd, null));
                // I'd like to use my boolean instead of this hard coding, but this is it for now
                if((myColor == ChessGame.TeamColor.WHITE && row == 2)||(myColor == ChessGame.TeamColor.BLACK && row == 7)) {
                    if (!board.isTileOccupied(new ChessPosition(row + (direction * 2), col))) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(row + (direction * 2), col), null));
                    }
                }
            }
            if(col - 1 > 0) {
                ChessPosition leftAttack = new ChessPosition(row + direction, col - 1);
                if(board.isTileOccupied(leftAttack) && board.getPiece(leftAttack).getTeamColor() != myColor) {
                    // attack to the left is possible
                    validMoves.add(new ChessMove(myPosition, leftAttack, null));
                }
            }
            if(col + 1 < 9) {
                ChessPosition rightAttack = new ChessPosition(row + direction, col + 1);
                if(board.isTileOccupied(rightAttack) && board.getPiece(rightAttack).getTeamColor() != myColor) {
                    // attack to the right is possible
                    validMoves.add(new ChessMove(myPosition, rightAttack, null));
                }
            }
        }
        return validMoves;
    }
}
