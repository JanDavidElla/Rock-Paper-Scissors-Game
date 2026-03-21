package depen;

public interface ChoiceStrategy {
    Move chooseMove(Player player, Move lastUserMove);
    String getStrategyName();

    default void onRoundEnd(Move humanMove, Move computerMove) {
        // default no-op
    }

    default void onGameEnd() {
        // default no-op
    }
}