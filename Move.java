
/**
 * Represents a move in the game, containing information about:
 * - The position of the move.
 * - The disc used for the move.
 * - A snapshot of the board state after the move.
 */
public class Move {
    private Position position;
    private  Disc disc;
    private Disc[][] boardmove;




    public Move(Disc[][] boardmove,Position position,Disc disc) {
        this.boardmove = boardmove;
        this.position = position;
        this.disc = disc;
    }

    public Move(Position position , Disc disc){
        this.position = position;
        this.disc = disc;
    }

    public void  setBoardmove(Disc[][] boardmove){
        this.boardmove = boardmove;
    }
    public void setPosition(Position position){
        this.position = position;
    }
    public void  setDisc(Disc disc){
        this.disc = disc;
    }
    public Disc[][] getBoardmove(){
        return this.boardmove;
    }
    //Gets the position of the move.
    public Position position(){
        return this.position;
    }
    //Gets the disc used in the move.
    public Disc disc() {
        return disc;
    }
}
