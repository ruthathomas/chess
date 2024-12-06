import ui.Repl;
import ui.WordArt;


public class Main {
    public static void main(String[] args) {
        var port = 8080;

        System.out.print(WordArt.WELCOME_TO_CHESS_SERIFED);

        if(args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new Repl(port).run();
    }
}