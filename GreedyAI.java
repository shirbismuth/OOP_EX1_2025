import java.util.List;

public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {////vg
        List<Position> validPos = gameStatus.ValidMoves();

        if (validPos == null) {// איו מהלכים חוקיים
            return null;
        }
        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();
        Disc currentDisc = new SimpleDisc(currentPlayer);
        Position bestpos = null;
        int maxFlips = 0;
        for (Position position : validPos) {
            int flips = gameStatus.countFlips(position);
            if (flips > maxFlips) {
                maxFlips = flips;
                bestpos = position;
            }
            if (flips == maxFlips) {
                if (position.col() > bestpos.col()) {
                    bestpos = position;
                } else if (position.col() == bestpos.col()) {
                    // השוואה לפי "ימינה" (עמודה גדולה יותר)
                    if (position.row() > bestpos.row()) {
                        bestpos = position;
                    }
                }
            }
        }
            // ביצוע המהלך
            if (bestpos != null){
                gameStatus.locate_disc(bestpos, currentDisc);
                return new Move( bestpos, currentDisc);
            }

            return null;
    }
}

