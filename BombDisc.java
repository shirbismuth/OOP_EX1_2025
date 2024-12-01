// This class represents a BombDisc, a special type of disc that may have unique effects in the game.
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
    // Returns the owner of the BombDisc.
    @Override
    public Player getOwner() {
        return this.player;
    }
    // Sets the owner of the BombDisc to the specified player.
    @Override
    public void setOwner(Player player) {
        this.player=player;

    }
    // Returns the visual representation (type) of the BombDisc.
    // The BombDisc is represented with the emoji "💣".
    @Override
    public String getType() {
        return "\uD83D\uDCA3";
    }
}
