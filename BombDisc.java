public class BombDisc implements Disc{
    private String type;
    private Player player;
    public BombDisc (Player player){
        this.player=player;
    }
    public BombDisc(BombDisc other) {
        this.type = other.type;
        this.player = other.player;
    }
    @Override
    public Player getOwner() {
        return this.player;
    }

    @Override
    public void setOwner(Player player) {
        this.player=player;

    }

    @Override
    public String getType() {
        return "\uD83D\uDCA3";
    }
}
