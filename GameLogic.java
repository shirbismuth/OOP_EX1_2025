import java.util.*;

public class GameLogic implements PlayableLogic {//,
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
    private static  Set<Position> setflip = new HashSet<>();
    private static Stack <Set<Position>> StackOfSetPosition;



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

    public void Bomb(Position bomb) {

        ArrayList<Position> processedPositions = new ArrayList<>();
        BombRecursive(bomb, processedPositions);


    }
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

                  if (disc != null &&Objects.equals(disc.getType(), "💣")) {
                    BombRecursive(pos, processedPositions);
                } }

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
                            board[row][col].setOwner(disc.getOwner());
                            Position position = new Position(row, col);
                            setflip.add(position);
                        }

                        if (board[row][col] != null && board[row][col].getType().equals("💣")) {
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



    @Override
    public boolean locate_disc(Position pos, Disc disc) {
        setflip.clear();
        if (!isValidMove(pos)) return false;
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



    // מתודה עזר לחישוב היפוכים בעקבות פצצה
    private int countBombFlips(Position bombPos, Set<Position> setbom) {
        countBombFlipsRecursive(bombPos, setbom);
        return setbom.size();
    }

    private void countBombFlipsRecursive(Position bombPos,Set<Position> setbom) {

        for (int[] direction : directions) {
            int row = bombPos.row() + direction[0];
            int col = bombPos.col() + direction[1];

             if (isWithinBounds(row, col)) {
                Position pos = new Position(row, col);
                Disc disc = getDiscAtPosition(pos);
                if (setbom.contains(pos)) continue;
                if (disc != null && !(disc instanceof UnflippableDisc)) {
                    if (disc.getOwner().isPlayerOne != isFirstPlayerTurn() ) {
                        setbom.add(pos);
                    }
                    if (disc instanceof BombDisc ) {
                         countBombFlipsRecursive(pos,setbom); // המשך חישוב לפצצות סמוכות
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

    @Override
    public void undoLastMove() {
        if (!player1.isHuman() || !player2.isHuman()) return;
        System.out.println("Undoing last move:");
        if (moves.size()==1) {
            System.out.println("\tNo previous move available to undo.");
            return;}// אין מהלך להחזיר אחורה

        Move lastMove = moves.pop();
        Set<Position> lastset = StackOfSetPosition.pop();

        if (lastMove != null && lastMove.position() != null) {
            Position pos = lastMove.position();
            board[pos.row()][pos.col()] = null; //// מחיקת הדיסק ממיקום זה
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




