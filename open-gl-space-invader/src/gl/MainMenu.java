package gl;

import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private MainSI game;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainMenu() {
        setTitle("Spaysse 1vadeur");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel menuPanel = new JPanel();
        JLabel titleLabel = new JLabel("Spaysse 1vadeur");
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 70));
        titleLabel.setForeground(Color.WHITE);

        JButton playButton = new JButton("Jouer");
        playButton.setPreferredSize(new Dimension(200, 50));
        playButton.setBackground(new Color(0      	 , 100, 0)); // dark red
        playButton.setForeground(Color.WHITE);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "game");
                startGame();
            }
        });

        JButton quitButton = new JButton("Quitter");
        quitButton.setPreferredSize(new Dimension(120, 35));
        quitButton.setBackground(new Color(100, 0, 0)); // dark red
        quitButton.setForeground(Color.WHITE);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JLabel controlLabel = new JLabel("Déplacements: Flèches gauche/droite | Attaque: Barre d'espace");
        controlLabel.setForeground(Color.WHITE);

        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 50, 0);
        menuPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        menuPanel.add(playButton, gbc);

        gbc.gridy = 2;
        menuPanel.add(quitButton, gbc);

        gbc.gridy = 3;
        menuPanel.add(controlLabel, gbc);

        cardPanel.add(menuPanel, "menu");
        add(cardPanel);

        setVisible(true);
    }

    private void startGame() {
        game = new MainSI();

        cardPanel.add(game, "game");
        cardLayout.show(cardPanel, "game");

        game.requestFocusInWindow();

        Animator animator = new Animator(game);
        animator.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
            }
        });
    }
}