import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.*;


public class GameClient extends JFrame {
    String side;
    ClientPanel panel;
    static String serverIp;
    static int serverPort;

    public GameClient(String side, String serverIp,int serverPort) {
        this.side = side;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 900);
        setLocationRelativeTo(null);
        panel = new ClientPanel(side);
        add(panel);
        setVisible(true);

        new ClientConnection(panel, side, serverIp, serverPort).start();
    }

    public static void main(String[] args) {
        String side = "right";
        new GameClient(side, serverIp, serverPort);
    }
}

class ClientPanel extends JPanel implements MouseMotionListener {
    private JPanel panelLeft, panelRight, panelBottom, ball, panel1, panel2, panel3;
    private int x = 0, y = 0;
    private int leftY = 0, rightY = 0, bottomX = 0;
    private int ballX = 400, ballY = 400;
    private String side;
    private PrintWriter output;
    private JLabel scoreLabel;
    private int leftScore = 3, rightScore = 3, bottomScore = 3;

    Image image = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "1314309.jpg");

    public void setLeftScore(int leftScore) {
        this.leftScore = leftScore;
    }

    public void setRightScore(int rightScore) {
        this.rightScore = rightScore;
    }

    public void setBottomScore(int bottomScore) {
        this.bottomScore = bottomScore;
    }

    public int getLeftScore() {
        return leftScore;
    }

    public int getRightScore() {
        return rightScore;
    }

    public int getBottomScore() {
        return bottomScore;
    }

    private boolean player1Eliminated = false;

    public boolean isPlayer1Eliminated() {
        return player1Eliminated;
    }

    public void setPlayer1Eliminated(boolean player1Eliminated) {
        this.player1Eliminated = player1Eliminated;
    }

    private boolean player2Eliminated = false;

    public boolean isPlayer2Eliminated() {
        return player2Eliminated;
    }

    public void setPlayer2Eliminated(boolean player2Eliminated) {
        this.player2Eliminated = player2Eliminated;
    }

    private boolean player3Eliminated = false;

    public boolean isPlayer3Eliminated() {
        return player3Eliminated;
    }

    public void setPlayer3Eliminated(boolean player3Eliminated) {
        this.player3Eliminated = player3Eliminated;
    }

    public ClientPanel(String side) {
        this.side = side;
        setLayout(null);
        addMouseMotionListener(this);

        panelLeft = new JPanel();
        panelRight = new JPanel();
        panelBottom = new JPanel();
        ball = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        scoreLabel = new JLabel(
                "<html><span style='font-size:24px;'>Scores: Left=0 | Right=0 | Bottom=0</span></html>");
        scoreLabel.setBounds(115, 11, 675, 125);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setOpaque(true); 
        scoreLabel.setBackground(new Color(50, 50, 50)); 
        scoreLabel.setHorizontalAlignment(JLabel.CENTER); 

        scoreLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(Color.WHITE, 2), // White border
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));

        add(scoreLabel);

        panelLeft.setSize(50, 100);
        panelLeft.setBackground(Color.RED);

        panelRight.setSize(50, 100);
        panelRight.setBackground(Color.BLUE);

        panelBottom.setSize(100, 50);
        panelBottom.setBackground(Color.GREEN);

        // ball.setSize(20, 20);
        // ball.setBackground(Color.ORANGE);

        add(panel1);
        add(panel2);
        add(panel3);
        add(panelLeft);
        add(panelRight);
        add(panelBottom);
        // add(ball);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        if (side.equals("left") && (leftScore == 0)) {
            y = 0;
        } else if (side.equals("right") && (rightScore == 0)) {
            y = 0;
        } else if (side.equals("bottom") && (bottomScore == 0)) {
            x = 0;
        }

        panel1.setBackground(new Color(100));
        panel1.setSize(900, 150);

        panel2.setBackground(new Color(100));
        panel2.setSize(150, 170);
        panel2.setLocation(0, 700);

        panel3.setBackground(new Color(100));
        panel3.setSize(150, 170);
        panel3.setLocation(736, 700);

        if (side.equals("left")) {
            panelLeft.setLocation(100, y - 50);
        } else if (side.equals("right")) {
            panelRight.setLocation(736, y - 50);
        } else if (side.equals("bottom")) {
            panelBottom.setLocation(x - 50, 700); 
        }

        // Update and display other players' bars (received from the server)
        if (!side.equals("left") && !player1Eliminated) {
            panelLeft.setLocation(100, leftY - 50);
        }
        if (!side.equals("right") && !player2Eliminated) {
            panelRight.setLocation(736, rightY - 50);
        }
        if (!side.equals("bottom") && !player3Eliminated) {
            panelBottom.setLocation(bottomX - 50, 700);
        }

        // Update and display ball position (received from the server)
        g.setColor(Color.ORANGE);
        g.fillOval(ballX, ballY, 20, 20);
    }

    // เปลี่ยนสีของผู้เล่น
    private Color getPlayerColor() {
        if (side.equals("left")) {
            return Color.RED;
        } else if (side.equals("right")) {
            return Color.BLUE;
        } else {
            return Color.GREEN;
        }
    }

    public void setWriter(PrintWriter writer) {
        this.output = writer;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if ((side.equals("left") && leftScore > 0)) {
            x = e.getX();
            y = e.getY();
        } else if ((side.equals("right") && rightScore > 0)) {
            x = e.getX();
            y = e.getY();
        } else if ((side.equals("bottom") && bottomScore > 0)) {
            x = e.getX();
            y = e.getY();
        }

        if ((side.equals("left") && player1Eliminated) ||
                (side.equals("right") && player2Eliminated) ||
                (side.equals("bottom") && player3Eliminated)) {
            return;
        }

        if ("left".equals(side)) {
            output.println("left:" + y);
        } else if ("right".equals(side)) {
            output.println("right:" + y);
        } else if ("bottom".equals(side)) {
            output.println("bottom:" + x);
        }

    }

    public void eliminatePlayer(String side) {
        if ("left".equals(side)) {
            player1Eliminated = true;
            panelLeft.setSize(50, getHeight());
            panelLeft.setLocation(100, 0);
        } else if ("right".equals(side)) {
            player2Eliminated = true;
            panelRight.setSize(50, getHeight());
            panelRight.setLocation(736, 0);
        } else if ("bottom".equals(side)) {
            player3Eliminated = true;
            panelBottom.setSize(getWidth(), 50);
            panelBottom.setLocation(0, 700); 
        }
        repaint();
    }

    public void updatePositions(String receivedSide, int position) {
        if ("left".equals(receivedSide)) {
            leftY = position; // Update the left bar's Y position
        } else if ("right".equals(receivedSide)) {
            rightY = position; // Update the right bar's Y position
        } else if ("bottom".equals(receivedSide)) {
            bottomX = position; // Update the bottom bar's X position
        }
        repaint();
    }

    public void updateScore(String scoreMessage) {
        String[] scoreParts = scoreMessage.replace("Scores: ", "").split(", ");

        for (String scorePart : scoreParts) {
            String[] playerScore = scorePart.split("=");
            String player = playerScore[0].trim();
            int score = Integer.parseInt(playerScore[1].trim());

            switch (player) {
                case "Left":
                    leftScore = score;
                    break;
                case "Right":
                    rightScore = score;
                    break;
                case "Bottom":
                    bottomScore = score;
                    break;
            }
        }

        String formattedScore = "<html>" +
                "<div style='text-align:center; background-color:#282828; color:white; padding:10px; border-radius:10px;'>"
                + "<span style='font-size:24px; font-weight:bold;'>Left: " + leftScore + "</span>" +
                " | " +
                "<span style='font-size:24px; font-weight:bold;'>Right: " + rightScore + "</span>" +
                " | " +
                "<span style='font-size:24px; font-weight:bold;'>Bottom: " + bottomScore + "</span>" +
                "</div></html>";

        scoreLabel.setText(formattedScore);
        repaint();
    }

    public void updateBallPosition(int ballX, int ballY) {
        this.ballX = ballX;
        this.ballY = ballY;
        repaint(); 
    }

    public void displayWinner(String winner) {
        String winnerMessage = "<html>" +
                "<div style='text-align:center; background-color:#282828; color:white; padding:10px; border-radius:10px;'>"
                + "<span style='font-size:30px; font-weight:bold;'>Winner: " + winner + "</span>"
                + "</div></html>";

        scoreLabel.setText(winnerMessage);
        repaint();
    }
}

class ClientConnection extends Thread {
    private ClientPanel panel;
    private String side;
    private String serverIp;
    private int serverPort;

    public ClientConnection(ClientPanel panel, String side, String serverIp, int serverPort) {
        this.panel = panel;
        this.side = side;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void run() {
        try {
            Socket socket = new Socket(serverIp, serverPort);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            output.println("side:" + side);
            panel.setWriter(output);

            String serverMessage;
            while ((serverMessage = input.readLine()) != null) {
                if (serverMessage.startsWith("ball:")) {
                    String[] parts = serverMessage.split(":");
                    int ballX = Integer.parseInt(parts[1]);
                    int ballY = Integer.parseInt(parts[2]);
                    panel.updateBallPosition(ballX, ballY);
                }
                if (serverMessage.startsWith("Scores")) {
                    panel.updateScore(serverMessage);
                } else if (serverMessage.contains(":eliminated")) {
                    String eliminatedSide = serverMessage.split(":")[0];
                    panel.eliminatePlayer(eliminatedSide);
                } else if (serverMessage.startsWith("winner:")) {
                    String winner = serverMessage.split(":")[1];
                    panel.displayWinner(winner);
                } else {
                    String[] parts = serverMessage.split(":");
                    if (parts.length == 2
                            && (parts[0].equals("left") || parts[0].equals("right") || parts[0].equals("bottom"))) {
                        int position = Integer.parseInt(parts[1]);
                        panel.updatePositions(parts[0], position);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}