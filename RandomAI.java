import java.util.*;


public class RandomAI extends AIPlayer {
    private  final  Random random = new Random();
    private final Map<Class<? extends Disc>, Integer> availableDiscs = new HashMap<>();


    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
        initializeDiscCounts();
    }
    private void initializeDiscCounts() {
        // אתחול כמויות הדיסקים בתחילת המשחק
        availableDiscs.put(SimpleDisc.class, Integer.MAX_VALUE); // דיסק רגיל תמיד זמין
        availableDiscs.put(BombDisc.class, number_of_bombs);
        availableDiscs.put(UnflippableDisc.class, number_of_unflippedable);
    }


    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> optionsPos = gameStatus.ValidMoves();

        if (gameStatus.ValidMoves()==null){// איו מהלכים חוקיים
            return null;
        }
        int randomIndex=  random.nextInt(optionsPos.size());
        Position Positionrandom = optionsPos.get(randomIndex);/// return  random position

        Disc discRandom = choosediscRandom();//לבחור סוג דל דיסק רנדומלית
        GameLogic gameLogic= new GameLogic();
        Disc[] []  bourd = gameLogic.getBoard();

        Move randomMove = new Move(bourd,Positionrandom,discRandom);
        return randomMove;
    }
    public  Disc choosediscRandom (){
        List<Class<? extends Disc>> availableTipe = new ArrayList<>();

        for(Map.Entry<Class<? extends Disc>, Integer> couples:availableDiscs.entrySet() ){//עובר בעצם על כל זוגות במערך
            if (couples.getValue()>0){
                availableTipe.add(couples.getKey());
            }
        }

        Class<? extends Disc> randomDisc = availableTipe.get(random.nextInt(availableTipe.size()));
        availableDiscs.put(randomDisc,availableDiscs.get(randomDisc)-1);//מוריד את כמות של אותו דיסק באחד
        if ( randomDisc == BombDisc.class){
            return new BombDisc(this);
        }if (randomDisc== UnflippableDisc.class){
            return new UnflippableDisc(this);
        }else {return new SimpleDisc(this);}

    }

}
