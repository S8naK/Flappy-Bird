import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener{
    
    int boardWidth = 394;
    int boardHeight = 700;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardWidth / 2 + 170;
    int birdWidth = 34;
    int birdHeight = 24;
    class Bird {
        
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
    
        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    
    class Pipe {
        
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean crossed = false;
    
        Pipe(Image img) {
            this.img = img;
        }
    }
    
    Bird bird;

    Timer timer;

    int velocityY = 0;
    int velocityX = -4;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Timer pipeTimer;
    Random random = new Random();

    boolean gameOver = false;
    double score = 0;
    public boolean gameStarted = false;  // Add a flag to track if the game has started

    GamePanel() {
        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        //this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(this);

        loadImgs();
    
        bird = new Bird(birdImg);

        pipes = new ArrayList<Pipe>();

        pipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        //pipeTimer.start();

        timer = new Timer(1000/60, this); //1000 ms = 1 s...60 frames per second
        //timer.start();
    }

    public void loadImgs() {
        backgroundImg = new ImageIcon(getClass().getResource("./background.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bluebird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) {
            g.setColor(Color.BLACK);  // Set text color
            g.setFont(new Font("Arial", Font.BOLD, 30));  // Set font size and style
            String message = "Press SPACE to start game";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(message);
            int textHeight = fm.getHeight();
            int x = (getWidth() - textWidth) / 2;  // Center the text horizontally
            int y = (getHeight() - textHeight) / 2;  // Center the text vertically
    
            g.drawString(message, x, y);
        }
        else {
            draw(g);
        }
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for(int i=0; i<pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(new Color(123,50,250));
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.PLAIN, 50));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over: " + String.valueOf((int) score), (boardWidth-metrics.stringWidth("Game Over: "))/2-10, boardHeight/2-120);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Press Space to Restart", 50, 340);
            g.drawString("Press F to Exit", 50, 380);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for(int i=0; i<pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.crossed && bird.x > pipe.x + pipe.width) {
                pipe.crossed = true;
                score += 0.5;
            }

            if(checkCollision(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int opening = boardHeight/5;
        
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + opening + pipeHeight;
        pipes.add(bottomPipe);
    }

    public boolean checkCollision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
        a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
        a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
        a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            pipeTimer.stop();
            timer.stop();
        }
    }
    
    //public boolean gameStarted = false;  // Add a flag to track if the game has started
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                // Start the game when space is pressed for the first time
                gameStarted = true;
                timer.start();
                pipeTimer.start();
            }  else if (!gameOver) {
            // Bird jumps when space is pressed during the game
                velocityY = -10;
            } else {
                // Restart the game after game over
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                timer.start();
                pipeTimer.start();
            }
        }

        if (gameOver && key == KeyEvent.VK_F) {
            // Exit the game if 'F' is pressed after game over
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

}

/*@Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE) {
            velocityY = -10;
            
            if(gameOver) {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                timer.start();
                pipeTimer.start();
            }
        }
        if (gameOver && e.getKeyCode() == KeyEvent.VK_F) {
            // Exit the game if 'F' is pressed after game over
            System.exit(0);
        }
    }*/