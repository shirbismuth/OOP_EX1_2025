import java.util.List;
// This class represents a greedy AI player that makes moves based on maximizing flips.
// It extends the AIPlayer class.
public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    // Method to determine the best move based on the game state.
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validPos = gameStatus.ValidMoves();

        if (validPos == null) {
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
                    if (position.row() > bestpos.row()) {
                        bestpos = position;
                    }
                }
            }
        }

            if (bestpos != null){
                gameStatus.locate_disc(bestpos, currentDisc);
                return new Move( bestpos, currentDisc);
            }

            return null;
    }
}

