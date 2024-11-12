import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    /**
     * The Main function to start the game.
     * Don't make any changes  lalalalalalalallala
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set the UIManager properties
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.select", new ColorUIResource(new Color(0, 0, 0, 0)));

            // Manually register all AI player subclasses
            AIPlayer.registerAllAIPlayers();

            // Create game logic
            PlayableLogic gameLogic = new GameLogic();

            // Create and show the game GUI
            GUI_for_chess_like_games gui = new GUI_for_chess_like_games(gameLogic, "Reversi Game");
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.pack();
            gui.setLocationRelativeTo(null); // Center on screen
            gui.setVisible(true);

            // Show player selection dialog
            Player[] players = initializePlayers(gui);

            // Initialize the game with selected players
            gameLogic.setPlayers(players[0], players[1]);

            // Restart the game
            gui.resetGame();
        });

    }
    private static Player[] initializePlayers(JFrame parent) {
        PlayerSelectionDialog dialog = new PlayerSelectionDialog(parent);
        dialog.setVisible(true);

        boolean isFirstPlayerHuman = dialog.isFirstPlayerHuman();
        boolean isSecondPlayerHuman = dialog.isSecondPlayerHuman();

        Player firstPlayer = isFirstPlayerHuman ? new HumanPlayer(true) : AIPlayer.createAIPlayer(dialog.getSelectedFirstPlayerAI(), true);
        Player secondPlayer = isSecondPlayerHuman ? new HumanPlayer(false) : AIPlayer.createAIPlayer(dialog.getSelectedSecondPlayerAI(), false);

        return new Player[]{firstPlayer, secondPlayer};
    }



    public static class PlayerSelectionDialog extends JDialog {
        private boolean isFirstPlayerHuman;
        private boolean isSecondPlayerHuman;
        private String selectedFirstPlayerAI;
        private String selectedSecondPlayerAI;

        public PlayerSelectionDialog(JFrame parent) {
            super(parent, "Player Selection", true);
            setLayout(new GridLayout(3, 2));

            // Create the list of options for the combo box (Human + AI types)
            List<String> playerOptions = new ArrayList<>();
            playerOptions.add("Human");
            playerOptions.addAll(AIPlayer.getAIPlayerTypes());

            // Convert the list to an array for the combo box
            String[] playerOptionsArray = playerOptions.toArray(new String[0]);

            JLabel firstPlayerLabel = new JLabel("First Player:");
            JComboBox<String> firstPlayerCombo = new JComboBox<>(playerOptionsArray);

            JLabel secondPlayerLabel = new JLabel("Second Player:");
            JComboBox<String> secondPlayerCombo = new JComboBox<>(playerOptionsArray);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                isFirstPlayerHuman = Objects.equals(firstPlayerCombo.getSelectedItem(), "Human");
                isSecondPlayerHuman = Objects.equals(secondPlayerCombo.getSelectedItem(), "Human");

                // If not human, get the selected AI type
                selectedFirstPlayerAI = isFirstPlayerHuman ? null : (String) firstPlayerCombo.getSelectedItem();
                selectedSecondPlayerAI = isSecondPlayerHuman ? null : (String) secondPlayerCombo.getSelectedItem();

                dispose();
            });

            // Add components to the dialog
            add(firstPlayerLabel);
            add(firstPlayerCombo);
            add(secondPlayerLabel);
            add(secondPlayerCombo);
            add(new JLabel()); // Empty label for spacing
            add(okButton);

            pack();
            setLocationRelativeTo(parent);
        }

        public boolean isFirstPlayerHuman() {
            return isFirstPlayerHuman;
        }

        public boolean isSecondPlayerHuman() {
            return isSecondPlayerHuman;
        }

        public String getSelectedFirstPlayerAI() {
            return selectedFirstPlayerAI;
        }

        public String getSelectedSecondPlayerAI() {
            return selectedSecondPlayerAI;
        }
    }


}