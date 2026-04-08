package depen;

import java.util.Random;

public class RandomStrategy implements ChoiceStrategy {
    private final Random random = new Random();

    @Override
    public PredictionSnapshot getPredictionSnapshot(Player player, Move lastUserMove) {
        Move[] moves = Move.values();
        return new PredictionSnapshot(null, moves[random.nextInt(moves.length)], null);
    }

    @Override
    public String getStrategyName() {
        return "Random AI";
    }
}
