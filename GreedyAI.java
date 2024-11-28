import java.util.List;

public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
<<<<<<< HEAD
    public Move makeMove(PlayableLogic gameStatus)
    {
        return null;
=======
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
>>>>>>> 4330f7b6f0e969f90c1083b7d876b37e7d1f22f0
    }
}

