package depen;

import java.util.Random;

public class RandomStrategy implements ChoiceStrategy {
    private final Random random = new Random();

    @Override
    public Move chooseMove(Player player, Move lastUserMove) {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }

    @Override
    public String getStrategyName() {
        return "Random AI";
    }
}
