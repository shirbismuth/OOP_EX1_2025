public class SimpleDisc implements Disc{
    private String type;
    private Player player;

    public SimpleDisc(Player player){
        this.player=player;
    }

    public SimpleDisc(SimpleDisc other) {
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
        return "⬤";

    }
}
