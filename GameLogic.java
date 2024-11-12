import java.util.List;

public class GameLogic implements PlayableLogic{
    private Disc[][] board;
    private int
    public GameLogic (){
        this.board=new Disc[8][8];
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        return false;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        int col=position.col();
        int row=position.row();
        Disc disc= this.board[row][col];
        return disc;
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    @Override
    public List<Position> ValidMoves() {
        return null;
    }

    @Override
    public int countFlips(Position a) {
        return 0;
    }

    @Override
    public Player getFirstPlayer() {
        return null;
    }

    @Override
    public Player getSecondPlayer() {
        return null;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {

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
