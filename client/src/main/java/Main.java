import ui.Repl;
import ui.WordArt;


public class Main {
    public static void main(String[] args) {
        var port = 8080;
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.print(WordArt.WELCOME_TO_CHESS_SERIFED);

        if(args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new Repl(port).run();
    }
}