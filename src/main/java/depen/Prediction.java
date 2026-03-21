package depen;

import java.util.ArrayList;
import java.util.Random;

public class Prediction implements ChoiceStrategy {
    private final Random random = new Random();

    @Override
    public Move chooseMove(Player player, Move lastUserMove) {
        if (player == null || player.getMoveHistory() == null || player.getMoveHistory().isEmpty()) {
            return getRandomMove();
        }

        return predictMove(player.getMoveHistory(), lastUserMove);
    }

    @Override
    public String getStrategyName() {
        return "Machine Learning";
    }

    public Move predictMove(ArrayList<Move> moveHistory, Move lastUserMove) {
        if (moveHistory.isEmpty() || lastUserMove == null) {
            return getRandomMove();
        }

        int rockCount = 0;
        int paperCount = 0;
        int scissorsCount = 0;

        for (int i = 0; i < moveHistory.size() - 1; i++) {
            Move move = moveHistory.get(i);
            Move sequentialMove = moveHistory.get(i + 1);

            if (move == lastUserMove) {
                switch (sequentialMove) {
                    case ROCK -> rockCount++;
                    case PAPER -> paperCount++;
                    case SCISSORS -> scissorsCount++;
                }
            }
        }

        if (rockCount == 0 && paperCount == 0 && scissorsCount == 0) {
            return getRandomMove();
        }

        if (rockCount >= paperCount && rockCount >= scissorsCount) {
            return Move.PAPER;
        } else if (paperCount >= rockCount && paperCount >= scissorsCount) {
            return Move.SCISSORS;
        } else {
            return Move.ROCK;
        }
    }

    private Move getRandomMove() {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }
}