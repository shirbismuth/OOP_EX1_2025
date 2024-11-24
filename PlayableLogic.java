

import java.util.List;

/**
 * The PlayableLogic interface defines the contract for the game logic
 * of chess-like games that can be played through a graphical user interface.
 * Implementing classes should provide the necessary logic to control the game,
 * manage player turns, and check for game completion.
 * DON'T MAKE ANY CHANGES HERE!
 */
public interface PlayableLogic {



    /**
     * Attempt to locate a disc on the game board.
     *
     * @param a The position for locating a new disc on the board.
     * @return true if the move is valid and successful, false otherwise.
     */
    boolean locate_disc(Position a, Disc disc);

    /**
     * Get the disc located at a given position on the game board.
     *
     * @param position The position for which to retrieve the disc.
     * @return The piece at the specified position, or null if no disc is present.
     */
    Disc getDiscAtPosition(Position position);

    /**
     * Get the size of the game board.
     *
     * @return The size of the game board, typically as the number of rows or columns.
     */
    int getBoardSize();

    /**
     * Get a list of valid positions for the current player.
     *
     * @return A list of valid positions where the current player can place a disc.
     */
    List<Position> ValidMoves();

    /**
     * The number of discs that will be flipped
     *
     * @return The number of discs that will be flipped if a disc will be placed in the 'a'.
     */
    int countFlips(Position a);

    /**
     * Get the first player.
     *
     * @return The first player.
     */
    Player getFirstPlayer();

    /**
     * Get the second player.
     *
     * @return The second player.
     */
    Player getSecondPlayer();

    /**
     * Set both players for the game.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    void setPlayers(Player player1, Player player2);

    /**
     * Check if it is currently the first player's turn.
     *
     * @return true if it's the first player's turn, false if it's the second player's turn.
     */
    boolean isFirstPlayerTurn();

    // Game state
    /**
     * Check if the game has finished, indicating whether a player has won or if it's a draw.
     *
     * @return true if the game has finished, false otherwise.
     */
    boolean isGameFinished();

    /**
     * Reset the game to its initial state, clearing the board and player information.
     */
    void reset();

    /**
     * Undo the last move made in the game, reverting the board state and turn order.
     * Works only with 2 Human Players, and does not work when AIPlayer is playing.
     */
    void undoLastMove();


}