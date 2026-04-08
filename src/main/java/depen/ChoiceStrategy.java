package depen;

public interface ChoiceStrategy {
    PredictionSnapshot getPredictionSnapshot(Player player, Move lastUserMove);
    String getStrategyName();

    default void observeRound(Move humanMove, Move computerMove) {
        // default no-op
    }
}
