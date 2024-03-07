import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel {
    private ArrayList<Block> blocks = new ArrayList<>();
    private int score = 0;
    private boolean gameActive = false;
    private int timeLeft = 10;
    private Random rand = new Random();
    private Timer timer;
    private JButton startButton = new JButton("Start Game");

    private int counter = 0;
    public void playSound(String soundName) {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception ex)
        {
            System.out.println("Error with playing sound.");
        }
    }
    public GamePanel() {
        setLayout(null);
        setFocusable(true);

        startButton.setBounds(300, 0, 100, 50);
        add(startButton);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                } else {
                    gameActive = false;
                    timer.stop();
                    startButton.setVisible(true);
                    playSound("gameover.wav");
                }
                repaint();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameActive) {
                    gameActive = true;
                    score = 0;
                    timeLeft = 10;
                    blocks.clear();
                    startButton.setVisible(false);
                    counter++;
                    timer.start();

                    generateBlock();
                    generateBlock();
                    generateBlock();
                    generateBlock();

                    repaint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameActive) return;

                playSound("gun-gunshot-01.wav");


                Iterator<Block> iterator = blocks.iterator();
                while (iterator.hasNext()) {
                    Block block = iterator.next();
                    if (block.rect.contains(e.getPoint())) {
                        if (block.powerUp) {
                            timeLeft += 2;
                        }
                        iterator.remove();
                        score++;
                        generateBlock();
                        break;
                    }
                }
                repaint();
            }
        });


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Game No: " + counter, 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Time remaining: " + timeLeft, 10, 60);


        if (!gameActive && timeLeft == 0) {
            g.drawString("Game over! Your score: " + score, getWidth() / 2 - 100, getHeight() / 2);
        }

        for (Block block : blocks) {
            g.setColor(block.color);
            g.fillRect(block.rect.x, block.rect.y, block.rect.width, block.rect.height);
        }
    }


    private void generateBlock() {
        if (!gameActive) return;
        int x = rand.nextInt(Math.max(1, this.getWidth() - 50));
        int y = rand.nextInt(Math.max(1, this.getHeight() - 50));
        int width = 20 + rand.nextInt(81);
        int height = 20 + rand.nextInt(81);
        Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        boolean powerUp = rand.nextInt(5) ==0;
        if (powerUp) {
            color = new Color(128, 0, 0);
        }
        blocks.add(new Block(new Rectangle(x, y, width, height), color, powerUp));
    }

    private static class Block {
        Rectangle rect;
        Color color;

        boolean powerUp;



        Block(Rectangle rect, Color color, boolean powerUp) {
            this.rect = rect;
            this.color = color;
            this.powerUp = powerUp;
        }
    }
}