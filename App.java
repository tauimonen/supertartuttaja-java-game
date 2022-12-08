import javax.swing.*;

class App {

    private static void initWindow() {
        JFrame window = new JFrame("Supertartuttaja - Super Spreader");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Board board = new Board();
        window.add(board);
        window.addKeyListener(board);
        window.setResizable(false);
        // fit the window size around the components
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        // invokeLater() is used here to prevent our graphics processing from
        // blocking the GUI. https://stackoverflow.com/a/22534931/4655368
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initWindow();
            }
        });
    }
}