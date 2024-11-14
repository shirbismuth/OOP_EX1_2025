import java.util.ArrayList;
import java.util.List;

public class GameLogic implements PlayableLogic {
    private Disc[][] board;
    private Player player1;
    private Player player2;

    public GameLogic() {
        this.board = new Disc[8][8];
        this.player1 = new HumanPlayer(true);
        this.player2 = new HumanPlayer(false) ;
    }
    @Override
    public boolean locate_disc(Position a, Disc disc) {
        return false;
    }

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

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}        , {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

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
        return 0;
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
        return false;
    }

    @Override
    public boolean isGameFinished() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void undoLastMove() {

    }
}




