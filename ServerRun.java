import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.Toolkit;

class ServerPanel extends JPanel {
    static List<PrintWriter> clientWriters = new ArrayList<>();
    private JPanel panelLeft, panelRight, panelBottom;
    private int panelLeftY = 0;
    private int panelRightY = 0;
    private int panelBottomX = 0;
    private JPanel panelWall_1;
    private JPanel panelWall_2;
    private JPanel panelWall_3;
    JPanel ball;
    int positionX = 400;
    int positionY = 400;
    int speedx;
    int speedy;
    int p1, p2, p3;
    JLabel jaaa;
    int t1, t2, t3;
    boolean player1Eliminated = false;
    boolean player2Eliminated = false;
    boolean player3Eliminated = false;
    boolean gameRunning = true;
    Image image = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "1314309.jpg");
    
    public ServerPanel() {
        setLayout(null); // Absolute layout
        setSize(900, 900);
        p1 = 3;
        p2 = 3;
        p3 = 3;
        t1 = 100;
        t2 = 100;
        t3 = 100;

        panelLeft = new JPanel();
        panelRight = new JPanel();
        panelBottom = new JPanel();

        panelWall_1 = new JPanel();
        panelWall_2 = new JPanel();
        panelWall_3 = new JPanel();
        ball = new JPanel();

        panelLeft.setSize(50, t1);
        panelLeft.setBackground(Color.RED);

        panelRight.setSize(50, t2);
        panelRight.setBackground(Color.BLUE);

        panelBottom.setSize(t3, 50);
        panelBottom.setBackground(Color.GREEN);

        panelWall_1.setLayout(null);
        // ball.setLocation(450, 450);

        jaaa = new JLabel();
        jaaa.setSize(700, 100);
        jaaa.setLocation(100, 30);
        jaaa.setFont(new Font("Serif", Font.BOLD, 45));
        jaaa.setForeground(Color.RED);
        panelWall_1.add(jaaa);

        add(panelWall_1);
        add(panelWall_2);
        add(panelWall_3);
        add(panelLeft);
        add(panelRight);
        add(panelBottom);
        //add(ball);

        Random random = new Random();
        speedx = random.nextInt(3) - 1;
        speedy = random.nextInt(3) - 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(),getHeight(),this);


        if ((player1Eliminated && player2Eliminated && !player3Eliminated) ||
                (player1Eliminated && player3Eliminated && !player2Eliminated) ||
                (player2Eliminated && player3Eliminated && !player1Eliminated)) {

            String winner = "";
            if (!player1Eliminated) {
                winner = "Player 1";
            } else if (!player2Eliminated) {
                winner = "Player 2";
            } else if (!player3Eliminated) {
                winner = "Player 3";
            }

            // ส่งชื่อผู้ชนะให้ client
            broadcastWinner(winner);
            gameRunning = false;
        }

        panelLeft.setLocation(100, panelLeftY);
        panelRight.setLocation(736, panelRightY);
        panelBottom.setLocation(panelBottomX, 700);

        panelWall_1.setBackground(new Color(100));
        panelWall_1.setSize(900, 150);

        panelWall_2.setBackground(new Color(100));
        panelWall_2.setSize(150, 170);
        panelWall_2.setLocation(0, 700);

        panelWall_3.setBackground(new Color(100));
        panelWall_3.setSize(150, 170);
        panelWall_3.setLocation(736, 700);

    
        g.setColor(Color.ORANGE);
        g.fillOval(positionX, positionY, 20, 20);

        jaaa.setText("Play 1 = " + p1 + "     Play 2 = " + p2 + "     Play 3 = " + p3);
        if ((player1Eliminated && player2Eliminated && !player3Eliminated) ||
                (player1Eliminated && player3Eliminated && !player2Eliminated) ||
                (player2Eliminated && player3Eliminated && !player1Eliminated)) {

            String winner = "";
            if (!player1Eliminated) {
                winner = "Player 1";
            } else if (!player2Eliminated) {
                winner = "Player 2";
            } else if (!player3Eliminated) {
                winner = "Player 3";
            }
            jaaa.setText(winner + " Wins!");
            gameRunning = false;
        }
    }

    public void updatePanelLeftY(int y) {
        this.panelLeftY = y - 50;
        repaint();
    }

    public void updatePanelRightY(int y) {
        this.panelRightY = y - 50;
        repaint();
    }

    public void updatePanelBottomX(int x) {
        this.panelBottomX = x - 50;
        repaint();
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void updateBallPosition() {

        positionX += speedx;
        positionY += speedy;

        Random random = new Random();
        if (speedx == 0 && speedy == 0) {
            speedy = -2; // Ensure the ball moves
        }

        if (positionY <= panelWall_1.getHeight()) {
            speedy = Math.abs(speedy); // Reverse Y direction if it hits the top wall
            int xx = random.nextInt(5) - 2;
            speedx = xx;
        }

        // Check for collision with panelWall_2 (left wall)
        if (positionX <= panelWall_2.getWidth() && positionY + 20 >= panelWall_2.getY()
                && positionY <= panelWall_2.getY() + panelWall_2.getHeight()) {
            // Reverse direction when the ball hits the left wall (panelWall_2)
            if (positionY <= panelWall_2.getY() + 20) {
                // ชนด้านบนของ panelWall_2
                speedx = -2; // -X
                speedy = -2; // -Y
            } else if (positionX <= panelWall_2.getX() + panelWall_2.getWidth()) {
                // ชนด้านขวาของ panelWall_2
                speedy = 2; // +Y
                speedx = 2; // +X
            }
        }

        // Check for collision with panelWall_3 (right wall)
        if (positionX + 25 >= panelWall_3.getX() &&
                positionY + 25 >= panelWall_3.getY() &&
                positionY <= panelWall_3.getY() + panelWall_3.getHeight()) {
            if (positionY <= panelWall_3.getY() + 25) {
                // ชนด้านบนของ panelWall_3
                speedx = 2; // +X
                speedy = -2; // -Y
            } else if (positionX + 20 >= panelWall_3.getX()) {
                // ชนด้านซ้ายของ panelWall_3
                speedy = 2; // +Y
                speedx = -2; // -X
            }
        }

        if (positionX <= 0 || positionX + 20 >= getWidth() || positionY <= 0 || positionY + 20 >= getHeight()) {
            if (speedx == 0 && speedy == 0) {
                speedy = -1; // Ensure the ball moves
            }
            if (positionX <= 0) {
                p1--;
            }
            if (positionX + 20 >= getWidth()) {
                p2--;
            }
            if (positionY + 20 >= getHeight()) {
                p3--;
            }
            positionX = getWidth() / 2 - 10; // 10 is half of the ball's width
            positionY = getHeight() / 2 - 10; // 10 is half of the ball's height

            speedx = random.nextInt(5) - 2; // Random speed between -1 and 1
            speedy = random.nextInt(5) - 2;

        }

        if (p1 == 0) {
            player1Eliminated = true;
            panelLeft.setSize(panelLeft.getWidth(), getHeight());
            panelLeftY = 0;
            broadcastElimination("left");
            repaint();
        }
        if (p2 == 0) {
            player2Eliminated = true;
            panelRight.setSize(panelRight.getWidth(), getHeight());
            panelRight.setLocation(736, 0);
            panelRightY = 0;
            broadcastElimination("right");
            repaint();

        }
        if (p3 == 0) {
            player3Eliminated = true;
            panelBottom.setSize(getWidth(), panelBottom.getHeight());
            panelBottom.setLocation(0, 700);
            panelBottomX = 0;
            broadcastElimination("bottom");
            repaint();
        }

        broadcastScores();
        checkCollisionWithBars();
        broadcastBallPosition();
        repaint();
    }

     private void checkCollisionWithBars() {
        int ballDiameter = 20; // ขนาดเส้นผ่านศูนย์กลางของลูกบอล
        Random random = new Random();
    
        // ชนกับ bar ฝั่งซ้าย (ตรวจสอบว่าบอลอยู่ด้านขวาของแถบก่อนที่จะชน)
        if (positionX <= panelLeft.getX() + panelLeft.getWidth() && 
            positionX + ballDiameter > panelLeft.getX() &&
            positionY + ballDiameter >= panelLeft.getY() && 
            positionY <= panelLeft.getY() + panelLeft.getHeight()) {
    
            // บอลชนกับแถบด้านซ้าย
            speedx = Math.abs(speedx); // ให้ลูกบอลเด้งไปทางขวา
            speedy = random.nextInt(5) - 2; // ปรับค่า speedY ให้เป็นแบบสุ่ม
        }
    
        // ชนกับ bar ฝั่งขวา (ตรวจสอบว่าบอลอยู่ด้านซ้ายของแถบก่อนที่จะชน)
        if (positionX + ballDiameter >= panelRight.getX() && 
            positionX < panelRight.getX() &&
            positionY + ballDiameter >= panelRight.getY() && 
            positionY <= panelRight.getY() + panelRight.getHeight()) {
    
            // บอลชนกับแถบด้านขวา
            speedx = -Math.abs(speedx); // ให้ลูกบอลเด้งไปทางซ้าย
            speedy = random.nextInt(5) - 2; // ปรับค่า speedY ให้เป็นแบบสุ่ม
        }
    
        // ชนกับ bar ฝั่งล่าง (ตรวจสอบว่าบอลอยู่ด้านบนของแถบก่อนที่จะชน)
        if (positionY + ballDiameter >= panelBottom.getY() &&
            positionY < panelBottom.getY() &&
            positionX + ballDiameter >= panelBottom.getX() && 
            positionX <= panelBottom.getX() + panelBottom.getWidth()) {
    
            // บอลชนกับแถบด้านล่าง
            speedy = -Math.abs(speedy); // ให้ลูกบอลเด้งขึ้นด้านบน
            speedx = random.nextInt(5) - 2; // ปรับค่า speedX ให้เป็นแบบสุ่ม
        }
    }

    private void broadcastElimination(String side) {
        String message = side + ":eliminated";
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private void broadcastBallPosition() {
        String message = "ball:" + positionX + ":" + positionY;
        synchronized (ServerPanel.clientWriters) {
            for (PrintWriter writer : ServerPanel.clientWriters) {
                writer.println(message); // Send the ball position to all connected clients
            }
        }
    }

    private void broadcastScores() {
        String scoreMessage = "Scores: Left=" + p1 + ", Right=" + p2 + ", Bottom=" + p3;
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(scoreMessage);
            }
        }
    }

    private void broadcastWinner(String winner) {
        String message = "winner:" + winner;
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter output;
    private ServerPanel serverPanel;
    private String side;

    public ClientHandler(Socket socket, ServerPanel serverPanel) {
        this.socket = socket;
        this.serverPanel = serverPanel;
    }

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            synchronized (ServerPanel.clientWriters) {
                ServerPanel.clientWriters.add(output);
            }

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                String[] parts = clientMessage.split(":");
                if (parts[0].equals("side")) {
                    this.side = parts[1];
                    System.out.println("Player has chosen side: " + this.side);
                } else if (parts.length == 2) {
                    String side = parts[0];
                    int position = Integer.parseInt(parts[1]);

                    if ("left".equals(side) && !serverPanel.player1Eliminated) {
                        serverPanel.updatePanelLeftY(position);
                    } else if ("right".equals(side) && !serverPanel.player2Eliminated) {
                        serverPanel.updatePanelRightY(position);
                    } else if ("bottom".equals(side) && !serverPanel.player3Eliminated) {
                        serverPanel.updatePanelBottomX(position);
                    }
                }

                // Broadcast the message to all clients
                synchronized (ServerPanel.clientWriters) {
                    for (PrintWriter writer : ServerPanel.clientWriters) {
                        writer.println(clientMessage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                synchronized (ServerPanel.clientWriters) {
                    ServerPanel.clientWriters.remove(output);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ServerRun extends JFrame {
    public static void main(String[] args) {
        Random random = new Random();
        ServerRun frame = new ServerRun();
        ServerPanel serverPanel = new ServerPanel();
        frame.add(serverPanel);
        frame.setSize(900, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Scanner scan = new Scanner(System.in);

        new Thread(() -> {
            if (scan.nextLine().equals("ok")) {
                while (serverPanel.gameRunning) {
                    serverPanel.updateBallPosition();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Waiting for clients to connect...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                new ClientHandler(socket, serverPanel).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}