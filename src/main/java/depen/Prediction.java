package depen;

import java.util.ArrayList;

public class Prediction extends Player {
    public Prediction() {
        super("computer", "Computer");
    }

    public Move predictMove(ArrayList<Move> moveHistory, Move lastUserMove) {
        if(moveHistory.isEmpty()) {
            return Move.ROCK;
        }

        int rockCount = 0;
        int paperCount = 0;
        int scissorsCount = 0;

        for(int i = 0; i < moveHistory.size() - 1; i++) {
            Move move = moveHistory.get(i);
            Move sequentialMove = moveHistory.get(i+1);

            if(move == lastUserMove) {
                switch (sequentialMove) {
                    case ROCK -> rockCount++;
                    case PAPER -> paperCount++;
                    case SCISSORS -> scissorsCount++;
                }
            }
            
        }

        if(rockCount >= paperCount && rockCount >= scissorsCount) { //If the user tends to pick rock, the computer will choose paper
            return Move.PAPER; 
        } else if(paperCount >= rockCount && paperCount >= scissorsCount) { //If the user tends to pick paper, the computer will choose scissors
            return Move.SCISSORS;
        } else {
            return Move.ROCK;
        }
    }
}
