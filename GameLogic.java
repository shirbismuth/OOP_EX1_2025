import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class GameLogic implements PlayableLogic {//לספור ניצחונות, להוסיף את האופציות של הפצצה באופציונלים
    private  Disc[][] board = new Disc[8][8];
    private Player player1;
    private Player player2;
    private boolean isPlayeroneturn = true;
    private final int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0,-1},            {0,1},
            {1, -1}, {1, 0}, {1, 1}
    };
    private static Stack <Move> moves;
    private int bombplayer2 = 3;
    private  int bombplayer1 = 3;





    public GameLogic() {
       this.player1 = new HumanPlayer(true);
        this.player2 = new HumanPlayer(false) ;
        this.isPlayeroneturn =true;
        this.moves= new Stack<Move>();
    }

    public GameLogic(Player player1,Player player2){
        this.player1=player1;
        this.player2=player2;
        this.moves= new Stack<Move>();
    }

    public void Bomb(Position bomb) {

        ArrayList<Position> processedPositions = new ArrayList<>();
        BombRecursive(bomb, processedPositions);

    }
    public void BombRecursive(Position bomb,ArrayList<Position> processedPositions) {
        if (processedPositions.contains(bomb)) {
            return; // אם המיקום כבר טופל, אין צורך להמשיך
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
                    } else
                        board[row][col].setOwner(player1);

                }

                if (disc != null &&Objects.equals(disc.getType(), "💣")) {
                    BombRecursive(pos, processedPositions);
                }}

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
                        if (!Objects.equals(board[row][col].getType(), "⭕")) {
                        board[row][col].setOwner(disc.getOwner());}
                        if (board[row][col] !=null &&board[row][col].getType().equals("💣")) {
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
    public boolean locate_disc(Position pos, Disc disc) {

        if (!isValidMove(pos)) return false;
//        if (moves.size() > 1) {
//            Move move = moves.pop();
//            moves.push(move);
//        }
        int bombsLeft = disc.getOwner().equals(player1) ? player1.number_of_bombs : player2.number_of_bombs;
        if (disc instanceof BombDisc && bombsLeft == 0) {
            return false;
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
        flip(pos, disc); // Flip the opponent discs
        isPlayeroneturn = !isPlayeroneturn;// Switch turn

        Disc[][] boardCopy = cloneBoard(board);
        Move move = new Move(boardCopy, pos, disc);
        moves.push(move);//#################

        return true;
    }


    @Override
    public Disc getDiscAtPosition(Position position) {
        int col = position.col();
        int row = position.row();
        if (!isWithinBounds(row, col)) {
            return null; // מחזיר null אם מחוץ לגבולות
        }
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
                   if (!(disc instanceof UnflippableDisc)){
                       if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                           count++; // Opponent disc found
                           if (disc instanceof BombDisc){
                               //דflips += count;
                               count = countBombFlips(new Position(row, col)); // חשב היפוכים מהפצצה
                               flips += count; // הוסף את ההיפוכים מהפצצה
                               break; // הפסק את הבדיקה בכיוון זה
                           }

                       } else {
                        flips += count; // Player's disc found after opponent discs
                            break;
                       }
                   }
                     row += rowDir;
                     col += colDir;
                     }
                }
        return flips;
    }
    // מתודה עזר לחישוב היפוכים בעקבות פצצה
    private int countBombFlips(Position bombPos) {
        int bombFlips = 0;
        ArrayList<Position> processedPositions = new ArrayList<>();
        bombFlips += countBombFlipsRecursive(bombPos, processedPositions);
        return bombFlips;
    }

    private int countBombFlipsRecursive(Position bombPos, ArrayList<Position> processedPositions) {
        if (processedPositions.contains(bombPos)) {
            return 0; // אם המיקום כבר טופל, אין צורך להמשיך
        }

        processedPositions.add(bombPos);
        int flips = 0;

        for (int[] direction : directions) {
            int row = bombPos.row() + direction[0];
            int col = bombPos.col() + direction[1];

            if (isWithinBounds(row, col)) {
                Position pos = new Position(row, col);
                Disc disc = getDiscAtPosition(pos);

                if (disc != null && !(disc instanceof UnflippableDisc)) {
                    if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                        flips++; // דיסק שהופך בעקבות הפצצה
                    }

                    if (disc instanceof BombDisc) {
                        flips += countBombFlipsRecursive(pos, processedPositions); // המשך חישוב לפצצות סמוכות
                    }
                }
            }
        }

        return flips;
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

    @Override
    public boolean isGameFinished() {
        if (ValidMoves().isEmpty()){
            return true;
        }
        return false;
    }

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

        System.out.println("player1 = " + player1.number_of_bombs);
        System.out.println("player2 = " + player1.number_of_bombs);

    }

    @Override
    public void undoLastMove() {
        if (moves.size()==1) return; // אין מהלך להחזיר אחורה

        Move lastMove = moves.pop();

        if (lastMove != null && lastMove.position() != null) {
            Position pos = lastMove.position();
            board[pos.row()][pos.col()] = null; // מחיקת הדיסק ממיקום זה
        }
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

                System.out.println("number of bombs left for player: " + isPlayeroneturn + "is " + corentp.number_of_unflippedable);
            }

            if (disc instanceof BombDisc) { // if disc is "kind of" bomb disc
                corentp.number_of_bombs++;

                System.out.println("number of bombs left for player: " + isPlayeroneturn + "is " + corentp.number_of_bombs);
            }

        }

        else
            System.out.println("No move was done");
    }



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
                    clone[i][j] = null; // אם התא ריק
                }
            }
        }
        return clone;
    }

}




