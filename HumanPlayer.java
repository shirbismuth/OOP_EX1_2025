public class HumanPlayer extends Player{


    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    boolean isHuman() {
        return true;
    }

}
