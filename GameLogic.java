import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class GameLogic implements PlayableLogic {//n
    private final Disc[][] board = new Disc[8][8];
    private Player player1;
    private Player player2;
    private boolean isPlayeroneturn = true;
    private final int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}        , {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };
    private static Stack <Move> moves;


    public GameLogic() {
       this.player1 = new HumanPlayer(true);
        this.player2 = new HumanPlayer(false) ;
        this.isPlayeroneturn =true;
        this.moves= new Stack<Move>();
    }

    public GameLogic(Player player1,Player player2){
        this.player1=player1;
        this.player2=player2;

    }

    public void Bomb(Position bomb) {
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            int row = bomb.row() + rowDir;
            int col = bomb.col() + colDir;
            if (isWithinBounds(row, col)) {
                Disc disc = getDiscAtPosition(new Position(row, col));
                if (disc != null && !Objects.equals(disc.getType(), "⭕") && disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                    if (Objects.equals(disc.getOwner(), player1)) {
                        board[row][col].setOwner(player2);
                    } else
                        board[row][col].setOwner(player1);

                }
                if (Objects.equals(disc.getType(), "💣")) {
                    Bomb(new Position(row, col));
                }

            }

        }
    }

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
                        board[row][col].setOwner(disc.getOwner());
                        if (board[row][col].getType().equals("💣")) {
                            Bomb(new Position(row, col));
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


//    public void flip(Position P, Disc disc) {
//        for (int[] direction : directions) {
//            int rowDir = direction[0];
//            int colDir = direction[1];
//            int row = P.row() + rowDir;
//            int col = P.col() + colDir;
//
//            // Ensure we're not out of bounds and the disc we're checking is not of the same color as the placed disc
//            if (isWithinBounds(row, col) && board[row][col] != null && board[row][col].getOwner() != disc.getOwner()) {
//
//                // Call recursive method to check if this direction has flippable discs
//                if (flipRecursive(row, col, rowDir, colDir, disc)) {
//                    // Once we know there's a valid sequence, start flipping the discs
//                    while (board[row][col].getOwner() != disc.getOwner()) {
//                        // Flip the discs
//                        board[row][col].setOwner(disc.getOwner());
//
//                        // Check if a bomb is encountered and trigger the Bomb logic
//                        if (board[row][col].getType().equals("💣")) {
//                            Bomb(new Position(row, col));
//                        }
//
//                        // Move to the next position in the same direction
//                        row += rowDir;
//                        col += colDir;
//                    }
//                }
//            }
//        }
//    }


//    private boolean flipRecursive(int row, int col, int rowDir, int colDir, Disc disc) {
//        if (!isWithinBounds(row, col) || board[row][col] == null) {
//            return false;
//        }
//        if (board[row][col] == disc) {
//            return true;
//        }
//        return flipRecursive(row + rowDir, col + colDir, rowDir, colDir, disc);
//    }
private boolean flipRecursive(int row, int col, int rowDir, int colDir, Disc disc) {
    // Base Case: If out of bounds or empty space, terminate recursion
    if (!isWithinBounds(row, col) || board[row][col] == null) {
        return false;
    }

    // Base Case: If we find the current player's disc, sequence is valid
    if (board[row][col].getOwner().equals(disc.getOwner())) {
        return true;
    }

    // Recursive Case: Keep traversing in the current direction
    if (flipRecursive(row + rowDir, col + colDir, rowDir, colDir, disc)) {
        // Flip the current disc if recursion returns true
        board[row][col].setOwner(disc.getOwner());
        System.out.println("Flipped disc at: (" + row + ", " + col + ")");
        return true;
    }

    // If no valid sequence, return false
    return false;
}






    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (!isValidMove(a)) return false;

        board[a.row()][a.col()] = disc; // Place the disc
        flip(a, disc); // Flip the opponent discs
        isPlayeroneturn = !isPlayeroneturn;// Switch turn
        return true;
    }

//    public boolean locate_disc(Position a, Disc disc) {
//
//        if (!isValidMove(a))
//            return false;
//
//        if (ValidMoves().contains(a))
//        {  board[a.row()][a.col()] = disc;
//
//            isPlayeroneturn = !isPlayeroneturn;
//            ValidMoves();
//            flip(a ,disc);
//            return true;
//
//        }
//       // this.moves.add()
//        else
//           return false;
//
//    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        int col = position.col();
        int row = position.row();
        return this.board[row][col];
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    @Override
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

    private boolean isValidMove (Position position) {

        if (getDiscAtPosition(position) != null) {
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

            if (disc == null) break;  // No disc found, invalid direction

            if (!disc.getOwner().equals(player)) {
                foundOpponentDisc = true;  // Found opponent's disc
            } else if (foundOpponentDisc) {
                return true;  // Found player's disc after opponent's, valid move
            } else {
                return false;  // Found player's disc without opponent in between
            }

            row += rowDir;
            col += colDir;
        }

        return false;  // No valid flip direction
    }
    @Override
    public int countFlips(Position a) {
        int flips = 0;
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            int row = a.row() + rowDir;
            int col = a.col() + colDir;
            int count = 0;

            while (isWithinBounds(row, col)) {
                Disc disc = getDiscAtPosition(new Position(row, col));
                if (disc == null) break; // No disc, stop counting
                if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                    count++; // Opponent disc found
                } else {
                    flips += count; // Player's disc found after opponent discs
                    break;
                }
                row += rowDir;
                col += colDir;
            }
        }
        return flips;
    }


    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < this.board.length && col >= 0 && col < this.board[0].length;
    }




//    public int countFlips(Position a) {
//        int flips = 0;
//
//        for (int[] direction : directions) {
//            int rowDir = direction[0];
//            int colDir = direction[1];
//            int row = a.row() + rowDir;
//            int col = a.col() + colDir;
//            int count = 0;
//
//
//            while (isWithinBounds(row, col)) {
//                Disc disc = getDiscAtPosition(new Position(row, col));
//                if (disc == null) break;
//
//                if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
//                    count++;
//                } else if (disc.getOwner().isPlayerOne == isFirstPlayerTurn()) {
//                    flips += count;
//
//                }
//
//                break;
//            }
//
//                row += rowDir;
//                col += colDir;
//            }
//
//
//
//
//
//            return flips;
//
//        }



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

    @Override
    public boolean isGameFinished() {
        if (ValidMoves().isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        board[3][3] = new SimpleDisc((player1));
        board[4][4] = new SimpleDisc((player1));
        board[3][4] = new SimpleDisc((player2));
        board[4][3] = new SimpleDisc((player2));

    }

    @Override
    public void undoLastMove() {


    }
}




