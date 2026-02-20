package quizisraahelmi;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Interface Swing Professionnelle - Avec options cliquables
 */
public class QuizClientGUI2 extends JFrame {

    private JLabel questionLabel;
    private JPanel optionsPanel;
    private JTextArea chatArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LamportClock clock;
    private String playerName;

    public QuizClientGUI2() {
        setTitle("Quiz Distribué");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        createMainMenu();
    }

    private void createMainMenu() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(168, 85, 247));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel titleLabel = new JLabel("Bienvenue à notre Quiz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        JButton playButton = createStyledButton("Jouer", 150, 50);
        JButton quitButton = createStyledButton("Quitter", 150, 50);

        playButton.addActionListener(e -> showPseudoScreen());
        quitButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 20, 10, 20);
        mainPanel.add(playButton, gbc);

        gbc.gridy = 2;
        mainPanel.add(quitButton, gbc);

        add(mainPanel);
    }

    private void showPseudoScreen() {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(168, 85, 247));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel titleLabel = new JLabel("Entrez votre nom");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JTextField pseudoField = new JTextField(20);
        pseudoField.setFont(new Font("Arial", Font.PLAIN, 16));
        pseudoField.setHorizontalAlignment(JTextField.CENTER);
        pseudoField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 1;
        panel.add(pseudoField, gbc);

        JButton startButton = createStyledButton("Commencer le quiz", 200, 50);
        JButton backButton = createStyledButton("Retour au menu", 200, 50);

        startButton.addActionListener(e -> {
            String pseudo = pseudoField.getText().trim();
            if (!pseudo.isEmpty()) {
                playerName = pseudo;
                showQuizScreen();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un pseudo !");
            }
        });

        backButton.addActionListener(e -> {
            getContentPane().removeAll();
            createMainMenu();
            revalidate();
            repaint();
        });

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 20, 10, 20);
        panel.add(startButton, gbc);

        gbc.gridy = 3;
        panel.add(backButton, gbc);

        add(panel);
        revalidate();
        repaint();
    }

    private void showQuizScreen() {
        getContentPane().removeAll();
        this.clock = new LamportClock();

        JPanel quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(Color.WHITE);

        // EN-TÊTE
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(59, 130, 246));
        headerPanel.setPreferredSize(new Dimension(900, 60));
        JLabel playerLabel = new JLabel("🎮 Joueur: " + playerName);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setForeground(Color.WHITE);
        headerPanel.add(playerLabel);
        quizPanel.add(headerPanel, BorderLayout.NORTH);

        // ZONE CENTRALE
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(243, 244, 246));
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel("<html><center>En attente de la première question...</center></html>");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setForeground(new Color(31, 41, 55));
        questionLabel.setHorizontalAlignment(JLabel.CENTER);
        questionLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        centerPanel.add(questionLabel, BorderLayout.NORTH);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBackground(new Color(243, 244, 246));
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane optionsScroll = new JScrollPane(optionsPanel);
        optionsScroll.setPreferredSize(new Dimension(800, 350));
        optionsScroll.setBorder(null);
        centerPanel.add(optionsScroll, BorderLayout.CENTER);

        quizPanel.add(centerPanel, BorderLayout.CENTER);

        // ZONE INFÉRIEURE
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel chatLabel = new JLabel(" Historique:");
        chatLabel.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(chatLabel, BorderLayout.NORTH);

        chatArea = new JTextArea(4, 50);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        bottomPanel.add(chatScroll, BorderLayout.CENTER);

        quizPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(quizPanel);
        revalidate();
        repaint();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerName);

            new Thread(this::readServerMessages).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur de connexion : " + e.getMessage());
        }
    }

    private void readServerMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                String finalMessage = message;
                SwingUtilities.invokeLater(() -> processServerMessage(finalMessage));
            }
        } catch (IOException e) {
            // Erreur
        }
    }

    private void processServerMessage(String message) {
        chatArea.append(message + "\n");

        if (message.contains("QUESTION|")) {
            handleQuestion(message);
        } else if (message.contains("OPTION|")) {
            handleOption(message);
        }
    }

    private void handleQuestion(String message) {
        String[] parts = message.split("\\|");
        if (parts.length >= 3) {
            String questionText = parts[2];
            questionLabel.setText("<html><center><b>" + questionText + "</b></center></html>");
            optionsPanel.removeAll();
            optionsPanel.revalidate();
            optionsPanel.repaint();
        }
    }

    private void handleOption(String message) {
        String[] parts = message.split("\\|");
        if (parts.length >= 3) {
            String optionNum = parts[1].trim();
            String optionText = parts[2].trim();

            JButton optionButton = new JButton("<html><center>" + optionNum + ") " + optionText + "</center></html>");
            optionButton.setFont(new Font("Arial", Font.BOLD, 14));
            optionButton.setForeground(new Color(30, 64, 175));
            optionButton.setBackground(new Color(219, 234, 254));
            optionButton.setBorder(new RoundBorder(8));
            optionButton.setFocusPainted(false);
            optionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionButton.setPreferredSize(new Dimension(700, 60));

            optionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    optionButton.setBackground(new Color(191, 219, 254));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    optionButton.setBackground(new Color(219, 234, 254));
                }
            });

            optionButton.addActionListener(e -> {
                clock.tick();
                out.println(optionNum + "|CLOCK|" + clock.getTime());

                for (Component comp : optionsPanel.getComponents()) {
                    comp.setEnabled(false);
                }
            });

            optionsPanel.add(optionButton);
            optionsPanel.revalidate();
            optionsPanel.repaint();
        }
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(new Color(30, 64, 175));
        button.setBackground(new Color(199, 210, 254));
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorder(new RoundBorder(10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(165, 180, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(199, 210, 254));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizClientGUI2 frame = new QuizClientGUI2();
            frame.setVisible(true);
        });
    }
}

class RoundBorder extends AbstractBorder {

    private final int radius;

    public RoundBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 5, 5, 5);
    }
}
