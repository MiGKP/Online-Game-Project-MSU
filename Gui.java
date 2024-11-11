import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Gui extends JFrame {

    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Main Lobby");
        MyPanel panel = new MyPanel(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

class MyPanel extends JPanel {
    Image bg = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + "/back.png");
    private JFrame parentFrame;
    private Clip clip;

    public MyPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Play background music
        playBackgroundMusic("in_game.wav");

        Dimension buttonSize = new Dimension(300, 100);

        // Start Button
        ImageIcon originalStartIcon = new ImageIcon("start.png");
        Image scaledStartImage = originalStartIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon startIcon = new ImageIcon(scaledStartImage);

        JButton startButton = new JButton("Start Game", startIcon);
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("pung.wav"); // Play sound when button is pressed
                showIpAndPortInputDialog();
            }
        });

        // Exit Button
        ImageIcon originalExitIcon = new ImageIcon("exit.png");
        Image scaledExitImage = originalExitIcon.getImage().getScaledInstance(310, 100, Image.SCALE_SMOOTH);
        ImageIcon exitIcon = new ImageIcon(scaledExitImage);

        JButton exitButton = new JButton("Exit", exitIcon);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("pung.wav"); // Play sound when button is pressed
                System.exit(0);
            }
        });

        // Add buttons to the panel
        add(Box.createVerticalGlue());
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(exitButton);
        add(Box.createVerticalGlue());
    }

    // Method to play background music
    private void playBackgroundMusic(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            if (!soundFile.exists()) {
                JOptionPane.showMessageDialog(this, "Background music file not found: " + soundFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
             clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the background music
            clip.start();

            // Reduce volume
            setVolume(-20.0f); // Reduce volume by 10 decibels
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error playing background music: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to set volume
    private void setVolume(float volume) {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume); // Set volume in decibels
        }
    }

    // Show IP input dialog
    private void showIpAndPortInputDialog() {
        // Create input fields for IP and port
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Server IP:"));
        panel.add(ipField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);

        // Show dialog for IP and port
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Server IP and Port",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String serverIp = ipField.getText();
            String portText = portField.getText();

            try {
                int port = Integer.parseInt(portText); // Parse port as an integer
                if (!serverIp.isEmpty() && port > 0 && port <= 65535) {
                    showSideSelection(serverIp, port); // If IP and port are valid, go to side selection
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid IP or Port!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Port must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Show side selection after IP input
    private void showSideSelection(String serverIp,int port) {
        JFrame selectionFrame = new JFrame("Select Your Side");
        selectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        selectionFrame.setSize(700, 400);
        selectionFrame.setLocationRelativeTo(null);

        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(new Color(60, 63, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        // Left Side Button
        JButton leftButton = new JButton("Left Side");
        leftButton.setFont(buttonFont);
        leftButton.setBackground(Color.RED);
        leftButton.setForeground(Color.WHITE);
        leftButton.setPreferredSize(new Dimension(175, 80));
        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("pung.wav"); 
               
                new GameClient("left", serverIp, port); // Pass the selected side and server IP
                if (clip.isRunning()){
                    clip.stop();
                 
                }
                
                 parentFrame.dispose(); // Close the main lobby
                selectionFrame.dispose(); // Close side selection
            }
        });

        // Right Side Button
        JButton rightButton = new JButton("Right Side");
        rightButton.setFont(buttonFont);
        rightButton.setBackground(Color.BLUE);
        rightButton.setForeground(Color.WHITE);
        rightButton.setPreferredSize(new Dimension(175, 80));
        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("pung.wav");
          
                new GameClient("right", serverIp, port); // Pass the selected side and server IP
                      clip.stop();
                      parentFrame.dispose();
                selectionFrame.dispose();
            }
        });

        // Bottom Side Button
        JButton bottomButton = new JButton("Bottom Side");
        bottomButton.setFont(buttonFont);
        bottomButton.setBackground(Color.GREEN);
        bottomButton.setForeground(Color.WHITE);
        bottomButton.setPreferredSize(new Dimension(175, 80));
        bottomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("pung.wav"); 
               
                new GameClient("bottom", serverIp, port); // Pass the selected side and server IP
                clip.stop();
             
                 parentFrame.dispose();
                selectionFrame.dispose();
            }
        });

        // Add buttons to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        selectionPanel.add(leftButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        selectionPanel.add(rightButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        selectionPanel.add(bottomButton, gbc);

        selectionFrame.add(selectionPanel);
        selectionFrame.setVisible(true);
    }

    // Method to play sound
    private void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            if (!soundFile.exists()) {
                JOptionPane.showMessageDialog(this, "File not found: " + soundFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error playing sound: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
