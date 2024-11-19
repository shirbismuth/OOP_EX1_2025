import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * IMPORTANT:
 * DON'T MAKE ANY CHANGES HERE
 */
public class GUI_for_chess_like_games extends JFrame {
    private static final int BUTTON_SIZE = 55;
    private static final int FONT_SIZE = 20;
    private final JButton[][] buttons;
    private final int BOARD_SIZE;
    private PlayableLogic gameLogic;
    private final JLabel turnLabel = new JLabel("Player 1's Turn");
    private final JLabel playerTowWinsLabel = new JLabel("Player 2 Wins: 0");
    private final JLabel playerOneWinsLabel = new JLabel("Player 1 Wins: 0");
    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private boolean isAITurn = false; // New flag to track AI turn
    private boolean bombKeyPressed = false;  // To track if 'b' key is pressed
    private boolean unflippedKeyPressed = false;  // To track if 'v' key is pressed
    private final JSlider aiSpeedSlider = new JSlider(JSlider.VERTICAL, 0, 2000, 1000);; // Slider to control AI response speed
    private boolean showColor = true;
    private boolean showNumbers = true;
    private final JCheckBox numbersCheckBox = new JCheckBox(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showNumbers = !showNumbers;

            updateBoard();
        }
    });

    Timer timer = new Timer(aiSpeedSlider.getValue(), e -> {
        Player currentPlayer = gameLogic.isFirstPlayerTurn() ? gameLogic.getFirstPlayer() : gameLogic.getSecondPlayer();

        // If it's an AI player's turn
        if (!currentPlayer.isHuman()) {

            Move aiMove = ((AIPlayer) currentPlayer).makeMove(gameLogic);
            preform_move((Position) aiMove.position(), aiMove.disc());

            // Update the current player after the move
            currentPlayer = gameLogic.isFirstPlayerTurn() ? gameLogic.getFirstPlayer() : gameLogic.getSecondPlayer();

            // If the next player is human, stop the timer
            if (currentPlayer.isHuman()) {
                ((Timer) e.getSource()).stop();  // Stop the timer
                isAITurn = false;  // Reset AI flag
            }
            updateBoard();
        }
        ((Timer) e.getSource()).setInitialDelay(aiSpeedSlider.getValue());
        ((Timer) e.getSource()).restart();
    });

    /**
     * Initializes the graphical user interface for the Chess like Games.
     *
     * @param gameLogic The game logic instance that controls the game's logic and state.
     */
    public GUI_for_chess_like_games(PlayableLogic gameLogic, String title) {
        super(title);
        this.gameLogic = gameLogic;
        this.BOARD_SIZE = gameLogic.getBoardSize();
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup key bindings for 'b' and 'v'
        setupKeyBindings();

        // Create a panel for the top section
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Add the AI speed slider - VERTICAL
        aiSpeedSlider.setMajorTickSpacing(500);
        aiSpeedSlider.setMinorTickSpacing(250);
        aiSpeedSlider.setPaintTicks(true);
        aiSpeedSlider.setPaintLabels(true);

        // The timer will repeat every second
        timer.setRepeats(true);

        // Create a panel with BoxLayout (Y_AXIS) to arrange components vertically
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        // Create a label for the slider
        JLabel sliderLabel = new JLabel("AI Speed:");

        // Create a label for the color checkbox
        JLabel colorLabel = new JLabel("Show Color:");

        // Create the checkbox for the color option
        JCheckBox colorCheckBox = new JCheckBox(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start the action when the checkbox is ticked
                showColor = !showColor;
                // Enable it when showColor is true
                numbersCheckBox.setEnabled(showColor);  // Disable the numbers checkbox when showColor is false
                updateBoard();  // Call a method to update the board when checkbox is ticked
            }
        });
        colorCheckBox.setSelected(true);
        numbersCheckBox.setSelected(true);
        // Add the label and the checkbox for color
        sliderPanel.add(colorLabel);
        sliderPanel.add(colorCheckBox);

        // Create a label for the numbers checkbox
        JLabel numbersLabel = new JLabel("Show Numbers:");

        // Add the label and the numbers checkbox below the color checkbox
        sliderPanel.add(numbersLabel);
        sliderPanel.add(numbersCheckBox);

        // Add the label and the slider to the panel
        sliderPanel.add(sliderLabel);
        sliderPanel.add(aiSpeedSlider);

        // Add the slider panel to the west of the frame
        add(sliderPanel, BorderLayout.WEST);


        // Adding the back button to the bottom of the main panel
        JButton backButton = new JButton("Back");
        topPanel.add(backButton, BorderLayout.WEST);

        // An action listener to the back button
        backButton.addActionListener(e -> {
            // implementation of the "Back Button"
            gameLogic.undoLastMove();
            updateBoard();
        });

        // Create left and right sub-panels for the win count labels
        JPanel leftLabelPanel = new JPanel();
        JPanel rightLabelPanel = new JPanel();

        // Add playerOneWinsLabel to the right sub-panel
        playerOneWinsLabel.setForeground(Color.blue);
        leftLabelPanel.add(playerOneWinsLabel);

        // Add playerTowWinsLabel to the left sub-panel
        playerTowWinsLabel.setForeground(Color.red);
        rightLabelPanel.add(playerTowWinsLabel);

        // Add the sub-panels to the top panel
        topPanel.add(leftLabelPanel, BorderLayout.WEST);
        turnLabel.setForeground(Color.blue);
        topPanel.add(turnLabel, BorderLayout.CENTER);
        topPanel.add(rightLabelPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        mainPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Adding the reset button to the bottom of the main panel
        JButton resetButton = new JButton("Reset");
        topPanel.add(resetButton, BorderLayout.EAST);

        // An action listener to the reset button
        resetButton.addActionListener(e -> {
            resetGame();
        });
        start();
    }
    // Set up key bindings for 'b' and 'v' keys
    private void setupKeyBindings() {
        // Get the input map for the main panel
        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getActionMap();

        // 'b' key press
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0, false), "bombPressed");
        actionMap.put("bombPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bombKeyPressed = true;
            }
        });

        // 'b' key release
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0, true), "bombReleased");
        actionMap.put("bombReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bombKeyPressed = false;
            }
        });

        // 'v' key press
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0, false), "unflippedPressed");
        actionMap.put("unflippedPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unflippedKeyPressed = true;
            }
        });

        // 'v' key release
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0, true), "unflippedReleased");
        actionMap.put("unflippedReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unflippedKeyPressed = false;
            }
        });
    }

    /**
     * This function is called when button is pressed.
     * @param newPosition The new disc position.
     */
    private void ButtonListener(Position newPosition) {
        if (isAITurn) {
            return; // Ignore human input when it is AI's turn
        }

        Player currentPlayer = gameLogic.isFirstPlayerTurn() ? gameLogic.getFirstPlayer() : gameLogic.getSecondPlayer();
        if (currentPlayer.isHuman()) {
            if (bombKeyPressed) {
                preform_move(newPosition, new BombDisc(currentPlayer));
            } else if (unflippedKeyPressed) {
                preform_move(newPosition, new UnflippableDisc(currentPlayer));
            } else {
                if (!preform_move(newPosition, new SimpleDisc(currentPlayer)))
                    return;
            }
        }
        currentPlayer = gameLogic.isFirstPlayerTurn() ? gameLogic.getFirstPlayer() : gameLogic.getSecondPlayer();

        if (!currentPlayer.isHuman())
            isAITurn = true;

        // Start the AI turn with a delay
        updateBoard();
        timer.setInitialDelay(aiSpeedSlider.getValue());

        // Restart the timer to trigger AI moves
        timer.restart();
    }

    // Return true only if preformed the move
    private boolean preform_move(Position move, Disc disc) {
        if (gameLogic.locate_disc(move, disc)) {
            // Check for victory after a move
            if (gameLogic.isGameFinished()) {
                resetGame();
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Updates the game board UI to reflect the current state of the game.
     * It updates the turn label, the button text, and the text color based on the current game state.
     * For each cell on the board, the button's appearance and text will be updated to match the corresponding
     * piece's position and type. The text color is determined by the piece's owner.
     *
     * @see #updateWinsLabels(int, int)
     */
    public void updateBoard() {
        // Update the turn label based on the attacker's turn
        if (!gameLogic.isFirstPlayerTurn()) {
            turnLabel.setForeground(Color.red);
            turnLabel.setText("Player 2's Turn");
        } else {
            turnLabel.setForeground(Color.blue);
            turnLabel.setText("Player 1's Turn");
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                // Set alternating background colors for a chessboard pattern
                if ((row + col) % 2 != 0) {
                    buttons[row][col].setBackground(new Color(0, 0, 0));
                } else {
                    buttons[row][col].setBackground(Color.WHITE);
                }
            }
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Disc disc = gameLogic.getDiscAtPosition(new Position(row, col));
                if (disc != null) {
                    // Update the button's appearance based on the new piece position
                    String type = disc.getType();
                    buttons[row][col].setText(type);
                    if ((disc.getOwner().isPlayerOne())) {
                        buttons[row][col].setForeground(Color.BLUE);
                    } else {
                        buttons[row][col].setForeground(new Color(255, 0, 0));
                    }
                } else {
                    buttons[row][col].setText("");
                }
            }
        }

        if (!isAITurn && showColor)
            enhanceColor();

        updateWinsLabels(gameLogic.getSecondPlayer().getWins(), gameLogic.getFirstPlayer().getWins());
    }

    private void enhanceColor() {
        List<Position> possiblePositions = this.gameLogic.ValidMoves();
        for (Position p : possiblePositions) {
            JButton button = buttons[p.row()][p.col()];
            Color currentColor = button.getBackground();

            // Determine if the current color is closer to white or black
            int brightness = (currentColor.getRed() + currentColor.getGreen() + currentColor.getBlue()) / 3;

            Color enhancedColor;
            if (brightness > 128) {
                // For lighter colors (closer to white)
                enhancedColor = new Color(199, 255, 199);  // Light green
            } else {
                // For darker colors (closer to black)
                enhancedColor = new Color(0, 100, 0);  // Dark green
            }

            button.setBackground(enhancedColor);
            if (showNumbers) {
                int num = gameLogic.countFlips(p);
                button.setForeground(Color.black);
                button.setText(String.valueOf(num));
            }
        }
    }


    private void updateWinsLabels(int attackerWins, int defenderWins) {
        playerTowWinsLabel.setText("Player 2 Wins: " + attackerWins);
        playerOneWinsLabel.setText("Player 1 Wins: " + defenderWins);
    }

    /**
     * Initializes and displays the game board with buttons for interactions.
     * Sets up button appearance, colors, and interactions with user input.
     * Highlights selected pieces and manages their movement based on user actions.
     */
    public void start() {
        // Your game logic to handle user interactions and updates
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE)); // Adjust size as needed
                Font chessFont = new Font("DejaVu Sans", Font.PLAIN, FONT_SIZE);
                buttons[row][col].setFont(chessFont);

                // Set alternating background colors for a chessboard pattern
                if ((row + col) % 2 != 0) {
                    buttons[row][col].setBackground(new Color(0, 0, 0));
                }
                else  {
                    buttons[row][col].setBackground(Color.WHITE);
                }

                // Clear default border and content area
                buttons[row][col].setBorderPainted(false);

                if (gameLogic.getDiscAtPosition(new Position(row, col)) != null) {
                    // Set the button's appearance based on the piece type
                    Disc disc = gameLogic.getDiscAtPosition(new Position(row, col));
                    String type = disc.getType();
                    buttons[row][col].setText(type);
                    if ((disc.getOwner().isPlayerOne())) {
                        buttons[row][col].setForeground(Color.BLUE);
                    } else {
                        buttons[row][col].setForeground(new Color(255, 0, 0));
                    }
                }

                mainPanel.add(buttons[row][col]);
                buttons[row][col].putClientProperty("row", row); // Store the row index
                buttons[row][col].putClientProperty("col", col); // Store the column index
                buttons[row][col].addActionListener((e) -> {
                    JButton clickedButton = (JButton) e.getSource();

                    int rowIndex = (int) clickedButton.getClientProperty("row");
                    int colIndex = (int) clickedButton.getClientProperty("col");
                    Position newPosition = new Position(rowIndex, colIndex);

                    ButtonListener(newPosition);  // Use the updated logic
                });

            }
        }
        if (!isAITurn && showColor)
            enhanceColor();
        // Add the main panel to the frame
        getContentPane().add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void resetGame() {
        gameLogic.reset();
        Player currentPlayer = gameLogic.isFirstPlayerTurn() ? gameLogic.getFirstPlayer() : gameLogic.getSecondPlayer();

        if (!currentPlayer.isHuman()) {
            ButtonListener(new Position(0, 0));
        }
        // Reset UI elements
        updateBoard();
        turnLabel.setText("Player 1's Turn");
    }
}


