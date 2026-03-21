package depen;

import com.google.gson.Gson;

public class GameRules {

    private int numOfRounds = 20;
    private Move lastUserMove = null;
    private int humanScore = 0;
    private int computerScore = 0;
    private int tieScore = 0;
    private Player currentPlayer = null;

    public GameRules() {
    }

    public GameRules(Player currentPlayer, int numOfRounds, int humanScore, int computerScore, int tieScore) {
        this.currentPlayer = currentPlayer;
        this.numOfRounds = numOfRounds;
        this.humanScore = humanScore;
        this.computerScore = computerScore;
        this.tieScore = tieScore;
    }

    public void playRound(ChoiceStrategy strategy, Input input, UserManagement um, Gson gson, DataLoader dl) {
        int currRound = 1;

        System.out.println("\nComputer strategy selected: " + strategy.getStrategyName());
        System.out.println("The game will be played for " + numOfRounds + " rounds.\n");

        while (currRound <= numOfRounds) {
            System.out.print("Round " + currRound + " - ");
            Move userMove = input.getInput();
            Move computerChoice = strategy.chooseMove(currentPlayer, lastUserMove);

            score(userMove, computerChoice, currentPlayer);

            System.out.println();
            lastUserMove = userMove;
            currRound++;
        }

        currentPlayer.setFavoriteMove();
        um.savePlayer(currentPlayer);
        Player[] finalPlayers = um.getPlayersAsArray();
        dl.store(gson, finalPlayers);

        printFinalResult();
    }

    public void score(Move userMove, Move computerChoice, Player player) {
        if (userMove == computerChoice) {
            tieScore++;
            printScore(userMove, computerChoice, "Draw!");
        } else if ((userMove == Move.ROCK && computerChoice == Move.SCISSORS)
                || (userMove == Move.PAPER && computerChoice == Move.ROCK)
                || (userMove == Move.SCISSORS && computerChoice == Move.PAPER)) {
            humanScore++;
            player.incrementWins();
            printScore(userMove, computerChoice, "You win this round!");
        } else {
            computerScore++;
            player.incrementLosses();
            printScore(userMove, computerChoice, "Computer wins this round!");
        }

        player.addMoveToHistory(userMove);
    }

    public String humanChoice(Move userMove) {
        return userMove.getName();
    }

    public String computerChoice(Move computerChoice) {
        return computerChoice.getName();
    }

    public void printScore(Move userMove, Move computerChoice, String win) {
        System.out.println("You chose " + humanChoice(userMove) + ". The computer chose "
                + computerChoice(computerChoice) + ". " + win);
        System.out.println("Score: Human: " + humanScore + " Computer: " + computerScore + " Draws: " + tieScore);
    }

    public void printFinalResult() {
        System.out.println("Game Over!");
        System.out.println("Final Score: Human: " + humanScore
                + " Computer: " + computerScore
                + " Draws: " + tieScore);

        if (humanScore > computerScore) {
            System.out.println("Overall Winner: Human");
        } else if (computerScore > humanScore) {
            System.out.println("Overall Winner: Computer");
        } else {
            System.out.println("Overall Result: Draw");
        }
    }
}
