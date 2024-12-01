// This class represents a SimpleDisc, a basic type of disc in the game.
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
    // Returns the owner of the disc (the player who owns it).
    @Override
    public Player getOwner() {
        return this.player;
    }
    // Sets the owner of the disc to the specified player.
    @Override
    public void setOwner(Player player) {
        this.player=player;

    }
    // Returns the visual representation (type) of the disc. Here, "⬤" is used.
    @Override
    public String getType() {
        return "⬤";

    }
}
