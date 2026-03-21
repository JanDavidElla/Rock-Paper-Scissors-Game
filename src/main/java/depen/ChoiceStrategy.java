package depen;

public interface ChoiceStrategy {
    Move chooseMove(Player player, Move lastUserMove);
    String getStrategyName();
}
