import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    private final int DELAY = 25;
    // controls the size of the board
    public static final int TILE_SIZE = 70;
    public static final int ROWS = 10;
    public static final int COLUMNS = 15;
    // controls how many viruses appear on the board
    public static final int NUM_COINS = 5;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    // objects that appear on the game board
    private Player player;
    private ArrayList<Virus> viruses;

    public Board() {
        playSound("sounds/8-bit.wav");
        // set the game board size
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // set the game board background color
        setBackground(new Color(255, 255, 255));

        // initialize the game state
        player = new Player();
        viruses = populateVirus();

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.

        // prevent the player from disappearing off the board
        player.tick();

        // give the player points for collecting viruses
        collectVirus();

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver
        // because Component implements the ImageObserver interface, and JPanel
        // extends from Component. So "this" Board instance, as a Component, can
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        // drawBackground(g);
        drawScore(g);
        for (Virus virus : viruses) {
            virus.draw(g, this);
        }
        player.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    /*
     * private void drawBackground(Graphics g) {
     * // draw a checkered background
     * g.setColor(new Color(214, 214, 214));
     * for (int row = 0; row < ROWS; row++) {
     * for (int col = 0; col < COLUMNS; col++) {
     * // only color every other tile
     * if ((row + col) % 2 == 1) {
     * // draw a square tile at the current row/column position
     * g.fillRect(
     * col * TILE_SIZE,
     * row * TILE_SIZE,
     * TILE_SIZE,
     * TILE_SIZE);
     * }
     * }
     * }
     * }
     */

    private void drawScore(Graphics g) {
        // set the text to be displayed
        String text = "POINTS: " + player.getScore();
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(128, 0, 0));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

    private ArrayList<Virus> populateVirus() {
        ArrayList<Virus> virusList = new ArrayList<>();
        Random rand = new Random();

        // create the given number of viruses in random positions on the board.
        // note that there is not check here to prevent two viruses from occupying the
        // same
        // spot, nor to prevent coins from spawning in the same spot as the player
        for (int i = 0; i < NUM_COINS; i++) {
            int virusX = rand.nextInt(COLUMNS);
            int virusY = rand.nextInt(ROWS);
            virusList.add(new Virus(virusX, virusY));
        }

        return virusList;
    }

    private void collectVirus() {
        // allow player to pickup coins
        ArrayList<Virus> collectedVirus = new ArrayList<>();
        for (Virus virus : viruses) {
            // if the player is on the same tile as a coin, collect it
            if (player.getPos().equals(virus.getPos())) {
                // give the player some points for picking this up
                selectRandomSound();
                player.addScore(1);
                collectedVirus.add(virus);
            }
        }
        // remove collected viruses from the board
        viruses.removeAll(collectedVirus);
    }

    private void selectRandomSound() {
        playSound("sounds/man-coughing.wav");

        /*
         * int randomNum = 1 + (int) (Math.random() * 5);
         * System.out.println(randomNum);
         * switch (randomNum) {
         * case 1:
         * playSound("sounds/man-coughing.wav");
         * break;
         * case 2:
         * playSound("sounds/man-sneeze.wav");
         * break;
         * case 3:
         * playSound("sounds/nose-blowing.wav");
         * case 4:
         * playSound("sounds/women-coughing.wav");
         * case 5:
         * playSound("sounds/women-sneeze.wav");
         * break;
         * default:
         * playSound("sounds/nose-blowing.wav");
         * }
         */
    }

    public void playSound(String soundFile) {
        Clip clip;
        AudioInputStream sound;
        try {
            File file = new File(soundFile);
            sound = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(sound);
            clip.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
