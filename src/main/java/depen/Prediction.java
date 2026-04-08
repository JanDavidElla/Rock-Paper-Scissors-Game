package depen;

import java.util.Random;

public class Prediction implements ChoiceStrategy {
    private static final String ML_FILE = "ml-data.json";

    private final Random random;
    private final PredictionModel model;
    private final PredictionStore store;

    public Prediction() {
        this(new Random(), new PredictionStore(ML_FILE));
    }

    Prediction(Random random, PredictionStore store) {
        this.random = random;
        this.store = store;
        this.model = new PredictionModel(store.load());
    }

    @Override
    public PredictionSnapshot getPredictionSnapshot(Player player, Move lastUserMove) {
        PredictionModel.PredictionDetail detail = model.predictHumanMove();
        Move predictedHumanMove = detail.predictedMove();
        Move computerMove = predictedHumanMove == null ? getRandomMove() : counterMove(predictedHumanMove);
        return new PredictionSnapshot(predictedHumanMove, computerMove, detail.confidence());
    }

    @Override
    public String getStrategyName() {
        return "Intelligent AI";
    }

    @Override
    public void observeRound(Move humanMove, Move computerMove) {
        model.recordRound(humanMove, computerMove);
        store.save(model.snapshotState());
    }

    private Move counterMove(Move predictedHumanMove) {
        return switch (predictedHumanMove) {
            case ROCK -> Move.PAPER;
            case PAPER -> Move.SCISSORS;
            case SCISSORS -> Move.ROCK;
        };
    }

    private Move getRandomMove() {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }
}
