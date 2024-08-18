import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int boardWidth = 394;
        int boardHeight = 700;
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        //GamePanel game = new GamePanel(boardWidth, boardHeight);
        GamePanel game = new GamePanel();
        frame.add(game);
        frame.pack();
        game.requestFocus();

        frame.setVisible(true);
    }
}
