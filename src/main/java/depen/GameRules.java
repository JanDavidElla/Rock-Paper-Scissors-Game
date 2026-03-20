package depen;

import java.util.Scanner;

public class GameRules {

    private int numOfRounds = 20;
    private Move lastUserMove = null;
    private int humanScore = 0;
    private int computerScore = 0;
    private int tieScore = 0;
    private Player currentPlayer = null;
    private Scanner scnr;

    //Default constructor
    public GameRules() { 
        
    }
    public GameRules(int numOfRounds, int humanScore, int computerScore, int tieScore, Scanner scnr) {
        this.numOfRounds = numOfRounds;
        this.humanScore = humanScore;
        this.computerScore = computerScore;
        this.tieScore = tieScore;
        this.scnr = scnr;

    }

    //could be moved to Input class
    public Move getValidMove(Scanner scnr) {
        while(true) {
            System.out.print("Choose (1 = rock, 2 = paper, 3 = scissors): ");

            if(!scnr.hasNextInt()) {
                System.out.println("Invalid input. Try again.");
                scnr.next();
                continue;
            }

            int userMove = scnr.nextInt();
            if(userMove >= 1 && userMove <= 3) {
                return Move.values()[userMove - 1]      ;
            } else {
                System.out.println("Invalid input. Try again.");
            }

        }
    }

    //--------------------------------------------------------


    /*
    Main round mechanism    
    */
    public void playRound(Move userMove, Move computerChoice, Player player) {
        int currRound = 1;
        while(currRound <= numOfRounds) {
            System.out.print("Round " + currRound + " - ");
            score(userMove, computerChoice, currentPlayer);
            System.out.println();
            lastUserMove = userMove;
            currRound++;
        } 
    }

    public void score(Move userMove, Move computerChoice, Player player) { //Main scoring mechanism
        if(userMove == computerChoice) {
            tieScore++;
            printScore(userMove, computerChoice, "Draw!"); 
        } else if((userMove == Move.ROCK && computerChoice == Move.SCISSORS) || (userMove == Move.PAPER && computerChoice == Move.ROCK) || (userMove == Move.SCISSORS && computerChoice == Move.PAPER)) {
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
        System.out.println("You chose " + humanChoice(userMove) + ". The computer chose " + computerChoice(computerChoice) + ". " + win);
        System.out.println("Score: Human: " + humanScore + " Computer: " + computerScore + " Draws: " + tieScore);
    }
}
