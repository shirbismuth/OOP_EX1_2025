import javax.swing.text.Position;

public class Move {
    private Position position;
    private   Disc disc;
    private Disc[][] boardmove;


    public Move(Disc[][] boardmove,Position position,Disc disc) {
        this.boardmove = boardmove;
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
    public Position position(){
        return this.position;
    }
    //public Disc getDisc(){return this.disc;}


    public Disc disc() {
        return null;
    }
}
