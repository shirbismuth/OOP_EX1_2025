public class Position {
    private int row;
    private int col;


    public Position (int col, int row){
        this.row=row;
        this.col=col;
    }
    public int row(){
        return row;
    }
    public int col(){
        return col;
    }
    public void setRow(int row){
        this.row=row;
    }
    public void setCol(int col){
        this.col=col;
    }

}
