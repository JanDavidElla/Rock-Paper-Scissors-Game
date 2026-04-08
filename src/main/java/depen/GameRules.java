package depen;

public class GameRules {

    public RoundOutcome determineOutcome(Move userMove, Move computerChoice) {
        if (userMove == computerChoice) {
            return RoundOutcome.TIE;
        }

        if ((userMove == Move.ROCK && computerChoice == Move.SCISSORS)
                || (userMove == Move.PAPER && computerChoice == Move.ROCK)
                || (userMove == Move.SCISSORS && computerChoice == Move.PAPER)) {
            return RoundOutcome.HUMAN_WIN;
        }

        return RoundOutcome.COMPUTER_WIN;
    }

    public String humanChoice(Move userMove) {
        return userMove.getName();
    }

    public String computerChoice(Move computerChoice) {
        return computerChoice.getName();
    }

    public String getRoundMessage(RoundOutcome outcome) {
        return switch (outcome) {
            case HUMAN_WIN -> "Human wins this round!";
            case COMPUTER_WIN -> "Computer wins this round!";
            case TIE -> "Tie round!";
        };
    }

    public String getOverallResultMessage(int humanScore, int computerScore) {
        if (humanScore > computerScore) {
            return "Overall winner: Human";
        }

        if (computerScore > humanScore) {
            return "Overall winner: Computer";
        }

        return "Overall result: Tie";
    }
}
