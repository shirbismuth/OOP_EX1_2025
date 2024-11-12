public class SimpleDisc implements Disc{
    private Player player;
    public SimpleDisc(Player player){
        this.player=player;
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
