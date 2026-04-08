package depen;

public interface ChoiceStrategy {
    Move chooseMove(Player player, Move lastUserMove);
    String getStrategyName();

    default PredictionSnapshot getPredictionSnapshot(Player player, Move lastUserMove) {
        return new PredictionSnapshot(null, chooseMove(player, lastUserMove), null);
    }

    default void onRoundEnd(Move humanMove, Move computerMove) {
        // default no-op
    }

    default void onGameEnd() {
        // default no-op
    }
}
