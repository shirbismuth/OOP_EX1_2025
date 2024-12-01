import java.util.*;

public class GameLogic implements PlayableLogic {
    private  Disc[][] board = new Disc[8][8];
    private Player player1;
    private Player player2;
    private boolean isPlayeroneturn = true; // Indicates whose turn it is (true for player1, false for player2)

    private final int[][] directions = { // Directions for flipping discs (8 directions)
            {-1, -1}, {-1, 0}, {-1, 1},
            {0,-1},            {0,1},
            {1, -1}, {1, 0}, {1, 1}
    };
    private static Stack <Move> moves; // Stack to store the history of moves made during the game
    private static  Set<Position> setflip = new HashSet<>(); // Set to track positions that have been flipped
    private static Stack <Set<Position>> StackOfSetPosition; // Stack to store the history of flipped positions



    public GameLogic() {
       this.player1 = new HumanPlayer(true);
        this.player2 = new HumanPlayer(false) ;
        this.isPlayeroneturn =true;
        this.moves= new Stack<Move>();
        this.StackOfSetPosition =new Stack<Set<Position>>();
    }

    public GameLogic(Player player1,Player player2){
        this.player1=player1;
        this.player2=player2;
        this.moves= new Stack<Move>();
    }
    public  Disc[][] getBoard(){
        return this.board;
    }

    /**
     * Handles the bomb action when a player places a bomb on the board.
     * It recursively processes the bomb's effect on surrounding positions.
     *
     * @param bomb The position of the bomb being placed.
     */
    public void Bomb(Position bomb) {

        ArrayList<Position> processedPositions = new ArrayList<>();
        BombRecursive(bomb, processedPositions);

    }
    /**
     * Recursively processes the bomb's effect on surrounding positions.
     * If a bomb is found, the method will recursively call itself to process neighboring bombs.
     *
     * @param bomb The current position of the bomb being processed.
     * @param processedPositions List of positions that have already been processed to prevent infinite loops.
     */
    public void BombRecursive(Position bomb,ArrayList<Position> processedPositions) {
        if (processedPositions.contains(bomb)) {
            return;
        }

        processedPositions.add(bomb);
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            int row = bomb.row() + rowDir;
            int col = bomb.col() + colDir;

            if (isWithinBounds(row, col)) {
                Position pos=new Position(row, col);
                Disc disc = getDiscAtPosition(pos);
                if (disc != null ){
                  if (!Objects.equals(disc.getType(), "⭕") && disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                    if (Objects.equals(disc.getOwner(), player1)) {
                        board[row][col].setOwner(player2);
                        setflip.add(pos);
                    } else
                        board[row][col].setOwner(player1);
                    setflip.add(pos);
                  }
                    // Recursively process if a bomb is found at the position
                  if (disc != null &&Objects.equals(disc.getType(), "💣")) {
                    BombRecursive(pos, processedPositions);
                } }

            }

        }
    }
    /**
     * Flips the opponent's discs in all directions from the specified position.
     * The discs are flipped if they form a valid chain according to the game rules.
     *
     * @param P The position of the disc to flip.
     * @param disc The disc to flip the opponent's discs with.
     */

    public void flip(Position P, Disc disc) {
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            int row = P.row() + rowDir;
            int col = P.col() + colDir;

            if (isWithinBounds(row, col) && board[row][col] != null && board[row][col].getOwner() != disc.getOwner()) {
                // Check if discs can be flipped in this direction
                if (canFlip(row, col, rowDir, colDir, disc)) {
                    // Flip discs in the direction

                    while (isWithinBounds(row, col) && board[row][col].getOwner() != disc.getOwner()) {
                        if (!Objects.equals(board[row][col].getType(), "⭕")) {
                            board[row][col].setOwner(disc.getOwner());
                            Position position = new Position(row, col);
                            setflip.add(position);
                        }

                        if (board[row][col] != null && board[row][col].getType().equals("💣")) {
                            Bomb(new Position(row, col)); // Trigger bomb if found
                        }
                        row += rowDir;
                        col += colDir;
                    }
                }

            }
        }
    }

    // Helper Method: Check if discs can be flipped in a specific direction
    private boolean canFlip(int row, int col, int rowDir, int colDir, Disc disc) {
        while (isWithinBounds(row, col)) {
            Disc currentDisc = board[row][col];
            if (currentDisc == null) return false; // No disc found
            if (currentDisc.getOwner() == disc.getOwner()) return true; // Valid chain
            row += rowDir;
            col += colDir;
        }
        return false;
    }



    @Override
    public boolean locate_disc(Position pos, Disc disc) {
        setflip.clear();
        if (!isValidMove(pos)) return false; // If the move is not valid, return false
        // Handle special discs like bombs or unflippable discs
        int bombsLeft = disc.getOwner().equals(player1) ? player1.number_of_bombs : player2.number_of_bombs;
        if (disc instanceof BombDisc && bombsLeft == 0) {
            return false; // No bombs left to place
        }
        if (disc instanceof BombDisc) {
            if (disc.getOwner().equals(player1)) {
                player1.number_of_bombs--;
                System.out.println("bombplayer1=" + player1.number_of_bombs);
            } else {
                player2.number_of_bombs--;
                System.out.println("bombplayer2=" + player2.number_of_bombs);
            }
        }
        // Handle unflippable discs
        int UnLeft = disc.getOwner().equals(player1) ?  player1.number_of_unflippedable : player2.number_of_unflippedable;
        if (disc instanceof UnflippableDisc && UnLeft == 0) {
            return false;
        }

        if (disc instanceof UnflippableDisc) {
            if (disc.getOwner().equals(player1)) {
                player1.number_of_unflippedable--;
                System.out.println("Unply1=" + player1.number_of_unflippedable);
            } else {
                player2.number_of_unflippedable--;
                System.out.println("Unply2=" + player2.number_of_unflippedable);
            }
        }

        board[pos.row()][pos.col()] = disc; // Place the disc
        if (disc.getOwner().equals(player1)) {
            System.out.println("Player 1 placed a "+ disc.getType()+" in ("+ pos.row()+","+pos.col()+")");
        } else {
            System.out.println("Player 2 placed a "+disc.getType()+" in ("+ pos.row()+","+pos.col()+")");
        }
        flip(pos, disc); // Flip the opponent discs
        isPlayeroneturn = !isPlayeroneturn;// Switch turn

        Disc[][] boardCopy = cloneBoard(board);
        Move move = new Move(boardCopy, pos, disc);
        moves.push(move);
        printflip(setflip);
        Set<Position> copySet = new HashSet<>(setflip);
        StackOfSetPosition.add(copySet);
        System.out.println( ""); ///**

        return true;
    }
    /**
     * Prints a message for each disc flipped during a move.
     *
     * @param setflip A set of Position objects representing the locations
     *                of the flipped discs on the board.
     */
public void printflip(Set<Position> setflip){
    for (Position position : setflip) {
        Disc discflip = board[position.row()][position.col()];
        if (discflip.getOwner().equals(player1)) {
            System.out.println("Player 1 flipped the " + discflip.getType() + " in (" + position.row() + "," + position.col() + ")");
        }  if (discflip.getOwner().equals(player2)){
            System.out.println("Player 2 flipped the " + discflip.getType() + " in (" + position.row() + "," + position.col() + ")");
        }

    }
}
    /**
     * Prints a message for each disc flipped back during an undo operation.
     *
     * @param setflip A set of Position objects representing the locations
     *                of the discs that were flipped back on the board.
     **/
    public void printundo(Set<Position> setflip){
        for (Position position : setflip) {
            Disc discflip = board[position.row()][position.col()];
            if (discflip.getOwner().equals(player1)) {
                System.out.println("\tUndo: flipping back " + discflip.getType() + " in (" + position.row() + "," + position.col() + ")");
            }  if (discflip.getOwner().equals(player2)){
                System.out.println("\tUndo: flipping back " + discflip.getType() + " in (" + position.row() + "," + position.col() + ")");
            }

        }
    }
/**
 * Retrieves the disc located at the specified position on the board.
 **/
 @Override
    public Disc getDiscAtPosition(Position position) {
        int col = position.col();
        int row = position.row();
        if (!isWithinBounds(row, col)) {
            return null;
        }
        return this.board[row][col];
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    @Override   //reteurns a list of all possible options
    public List<Position> ValidMoves() {
        List<Position> validPositions = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                Position position = new Position(row, col);
                if (isValidMove(position)) {
                    validPositions.add(position);
                }
            }
        }

        return validPositions;
    }
    /**
     * Determines all valid moves for the current player on the board.
     * A move is considered valid if placing a disc at the position will
     * capture at least one opponent's disc according to the game's rules.
     **/
     private boolean isValidMove (Position position) {

        if (getDiscAtPosition(position) != null) {
            return false;
        }
        if (countFlips(position)==0){
            return false;
        }
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            if (checkDirection(position, rowDir, colDir)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkDirection(Position position, int rowDir, int colDir) {
        Player player = isFirstPlayerTurn() ? getFirstPlayer() : getSecondPlayer();
        int row = position.row() + rowDir;
        int col = position.col() + colDir;
        boolean foundOpponentDisc = false;

        while (isWithinBounds(row, col)) {
            Disc disc = getDiscAtPosition(new Position(row, col));

            if (disc == null) return false;  // No disc found, invalid direction

            // Found player's disc without opponent in between
            if (!disc.getOwner().equals(player)) {
                foundOpponentDisc = true;  // Found opponent's disc
            } else
                return foundOpponentDisc;  // Found player's disc after opponent's, valid move

            row += rowDir;
            col += colDir;
        }

        return false;  // No valid flip direction
    }

    @Override
    public int countFlips(Position a) {//ss
        Set<Position> allpos = new HashSet<>();
        for (int[] direction : directions) {
            int row = a.row() + direction[0];
            int col = a.col() + direction[1];
            Set<Position> addpos = new HashSet<>();

            while (isWithinBounds(row, col)) {
                Position pos = new Position(row, col);
                Disc disc = getDiscAtPosition(pos);
                if (disc == null) break; // No disc, stop counting
                if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                    if (!(disc instanceof UnflippableDisc)) {
                        addpos.add(pos);
                        if (disc instanceof BombDisc)
                            countBombFlips(pos, addpos);
                    }
                } else {
                    allpos.addAll(addpos); // Player's disc found after opponent discs
                    break;
                }

                row += direction[0];
                col += direction[1];
            }


        }


        return allpos.size();
    }




    private int countBombFlips(Position bombPos, Set<Position> setbom) {
        countBombFlipsRecursive(bombPos, setbom);
        return setbom.size();
    }
    /**
     * Recursively calculates all positions of discs that will be flipped as a result of a bomb disc's explosion.
     * Bomb discs trigger chain reactions, affecting all adjacent discs and potentially other bomb discs.
     **/
    private void countBombFlipsRecursive(Position bombPos,Set<Position> setbom) {

        for (int[] direction : directions) {
            int row = bombPos.row() + direction[0];
            int col = bombPos.col() + direction[1];

             if (isWithinBounds(row, col)) { // Ensures the position is within the board boundaries.
                Position pos = new Position(row, col);
                Disc disc = getDiscAtPosition(pos);
                if (setbom.contains(pos)) continue;
                if (disc != null && !(disc instanceof UnflippableDisc)) {
                    if (disc.getOwner().isPlayerOne != isFirstPlayerTurn() ) {
                        setbom.add(pos);
                    }
                    if (disc instanceof BombDisc ) {
                        // Recursively triggers the explosion effect for adjacent bomb discs.
                         countBombFlipsRecursive(pos,setbom);
                    }

                }
            }
        }

    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < this.board.length && col >= 0 && col < this.board[0].length;
    }



    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 =player1;
        this.player2 =player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return isPlayeroneturn;
    }
    /**
     * Determines whether the game is finished.
     * - Checks if there are any valid moves left. If so, the game is not finished.
     * - If no moves are left, counts the discs on the board for each player.
     * - Declares the winner based on the higher disc count or determines a tie.
     * - Updates the win count for the winning player.
     *
     * @return `true` if the game is finished, `false` otherwise.
     * Outputs the result of the game to the console.
     */
    @Override
    public boolean isGameFinished() {
        if (!ValidMoves().isEmpty()){
            return false;
        }
        int player1Count = 0;
        int player2Count = 0;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    Disc disc = board[i][j];
                    if (disc != null) {
                        if (disc.getOwner().equals(player1)) {
                            player1Count++;
                        } else if (disc.getOwner().equals(player2)) {
                            player2Count++;
                        }
                    }
                }
            }

            if (player1Count > player2Count) {
                player1.wins++;
                System.out.println("Player 1 wins with "+player1Count+ " discs! Player 2 had "+player2Count + " discs");
            } else if (player2Count > player1Count) {
                player2.wins++;
                System.out.println("Player 2 wins with "+player2Count+ " discs! Player 1 had "+player1Count + " discs");
            } else {
                System.out.println("It's a tie!");
                return true;
            }



        return true;
    }
    /**
     * Resets the game board to its initial state.
     * - Clears the board by setting all positions to `null`.
     * - Reinitializes the starting four discs at the center of the board.
     * - Creates an initial move to preserve the starting board state.
     * - Resets the number of special discs (BombDisc and UnflippableDisc) for both players.
     * - Sets the turn to Player 1.
     */
    @Override
    public void reset() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = null; }
        }
        moves.removeAll(moves);
        board[3][3] = new SimpleDisc((player1));
        board[4][4] = new SimpleDisc((player1));
        board[3][4] = new SimpleDisc((player2));
        board[4][3] = new SimpleDisc((player2));
        Disc[][] boardCopy = cloneBoard(board);
        Move move = new Move(boardCopy, null, null);
        moves.add(move);

        player1.number_of_bombs=3;
        player2.number_of_bombs=3;
        player1.number_of_unflippedable=2;
        player2.number_of_unflippedable=2;

        isPlayeroneturn = true;

    }
    /**
     * Reverts the last move performed in the game.
     * - If no moves have been made, or if one of the players is not human, the action is skipped.
     * - Removes the last placed disc from the board and restores the previous game state.
     * - Updates the special disc counts for the current player (BombDisc and UnflippableDisc).
     * - Reverses all flips caused by the last move.

     */
    @Override
    public void undoLastMove() {
        if (!player1.isHuman() || !player2.isHuman()) return;
        System.out.println("Undoing last move:");
        if (moves.size()==1) {
            System.out.println("\tNo previous move available to undo.");
            return;}

        Move lastMove = moves.pop();
        Set<Position> lastset = StackOfSetPosition.pop();

        if (lastMove != null && lastMove.position() != null) {
            Position pos = lastMove.position();
            board[pos.row()][pos.col()] = null;
            System.out.println("\tUndo: removing "+lastMove.disc().getType()+" fron ("+pos.row()+","+pos.col()+")"); ///**
        }
        printundo(lastset);
        if (moves.isEmpty())
            reset();
            else{

            board = cloneBoard(moves.peek().getBoardmove());}
         //   board = moves.peek().getBoardmove();}

        if (lastMove != null) {
            isPlayeroneturn = !isPlayeroneturn; // change turn
            Disc disc = lastMove.disc();

            Player corentp = isPlayeroneturn ? player1 : player2;
            if (disc instanceof UnflippableDisc) { // if disc is "kind of" bomb disc
                corentp.number_of_unflippedable++;

            }

            if (disc instanceof BombDisc) { // if disc is "kind of" bomb disc
                corentp.number_of_bombs++;


            }

        } else{
            System.out.println("No move was done");}
        System.out.println("");
    }

    /**
     * Creates a deep copy of the current game board.
     * - Iterates through each cell in the board.
     * - For each disc, creates a new instance of its respective type (SimpleDisc, BombDisc, UnflippableDisc).
     * - Ensures that the copied board is independent of the original board.
     *
     * @param board The current game board to be cloned.
     * @return A new 2D array representing the cloned board state.
     */
    private Disc[][] cloneBoard(Disc[][] board) {
        Disc[][] clone = new Disc[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    Disc originalDisc = board[i][j];
                    if (originalDisc instanceof SimpleDisc) {
                        clone[i][j] = new SimpleDisc((SimpleDisc) originalDisc);
                    } else if (originalDisc instanceof UnflippableDisc) {
                        clone[i][j] = new UnflippableDisc((UnflippableDisc) originalDisc);
                    } else if (originalDisc instanceof BombDisc) {
                        clone[i][j] = new BombDisc((BombDisc) originalDisc);
                    }
                } else {
                    clone[i][j] = null;
                }
            }
        }
        return clone;
    }

}




