import java.util.List;

public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        if (gameStatus.ValidMoves() == null) {// איו מהלכים חוקיים
            return null;
        }
        List<Position> validPositions = gameStatus.ValidMoves(); // קבלת כל הפוזיציות האפשריות
        GameLogic gameLogic = new GameLogic()
        Disc[][] board = gameStatus.();
        Move bestMove = null;
        int maxFlipped = 0;
        for (Position position : validPositions) {
            // חשב את מספר הדיסקיות שיהפכו במהלך הזה
            int flippedDiscs = gameLogic.countFlips(position);

        }return null;
    }
    }
    }
}

