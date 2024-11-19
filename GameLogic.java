import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class GameLogic implements PlayableLogic {//n
    private final Disc[][] board = new Disc[8][8];
    private Player player1;
    private Player player2;
    private boolean isPlayeroneturn;
    private final int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}        , {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };
    private Stack <Move> moves;


    public GameLogic() {
       this.player1 = new HumanPlayer(true);
        this.player2 = new HumanPlayer(false) ;
        this.isPlayeroneturn =true;
    }

    public GameLogic(Player player1,Player player2){
        this.player1=player1;
        this.player2=player2;
        this.isPlayeroneturn =true;

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

    public void flip(Position flip, Disc disc) {
        for (int[] direction : directions) {
            int rowDir = direction[0];
            int colDir = direction[1];
            int row = flip.row() + rowDir;
            int col = flip.col() + colDir;
            if (isWithinBounds(row, col) && board[row][col].getOwner() != disc.getOwner()) {
                if (flipRecursive(row, col, rowDir, colDir, disc)) {
                    while (board[row][col].getOwner() != disc.getOwner()) {
                        if (disc.getOwner() == player1) {
                            board[row][col].setOwner(player1);
                        } else
                            board[row][col].setOwner(player2);
                        if ( Objects.equals( board[row][col].getType(), "💣")){
                            Bomb(new Position(row ,col));
                        }
                        row += rowDir;
                        col += colDir;
                    }
                }
            }
        }
    }


    private boolean flipRecursive(int row, int col, int rowDir, int colDir, Disc disc) {
        if (!isWithinBounds(row, col) || board[row][col] == null) {
            return false;
        }
        if (board[row][col] == disc) {
            return true;
        }
        return flipRecursive(row + rowDir, col + colDir, rowDir, colDir, disc);
    }





    @Override
    public boolean locate_disc(Position a, Disc disc) {
       // if (ValidMoves().contains(a)) {
            board[a.row()][a.col()] = disc;
            isPlayeroneturn = !isPlayeroneturn;
        return true;

    }
        //else
         //   return false;



    @Override
    public Disc getDiscAtPosition(Position position) {
        int col = position.col();
        int row = position.row();
        Disc disc = this.board[row][col];
        return disc;
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    @Override
    public List<Position> ValidMoves() {
        List<Position> validPositions = new ArrayList<>();
        int boardSize = this.getBoardSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
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


        for (int i = 0; i < directions.length; i++) {
            int rowDir = directions[i][0];
            int colDir = directions[i][1];
            if (checkDirection(position, rowDir, colDir)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkDirection(Position position, int rowDir, int colDir) {
        int row = position.row() + rowDir;
        int col = position.col() + colDir;
        boolean foundOpponentDisc = false;

        while (isWithinBounds(row, col)) {
            Disc disc = getDiscAtPosition(new Position(row, col));


            if (disc == null) {
                return false;
            }


            if (disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                foundOpponentDisc = true;
            } else {

                return foundOpponentDisc;
            }

            row += rowDir;
            col += colDir;
        }

        return false;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < this.board.length && col >= 0 && col < this.board[0].length;
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


                    if (disc != null && disc.getOwner().isPlayerOne != isFirstPlayerTurn()) {
                        count++;
                    }

                    else if (disc != null && disc.getOwner().isPlayerOne == isFirstPlayerTurn()) {
                        flips += count;
                        break;
                    }
                    else {
                        break;
                    }

                    row += rowDir;
                    col += colDir;
                }
            }

            return flips;
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
        board[3][3] = new SimpleDisc((player1));
        board[4][4] = new SimpleDisc((player1));
        board[3][4] = new SimpleDisc((player2));
        board[4][3] = new SimpleDisc((player2));

    }

    @Override
    public void undoLastMove() {


    }
}




