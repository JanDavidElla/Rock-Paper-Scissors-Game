package depen;

import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class GameRules {

    private int numOfRounds = 20;
    private Move lastUserMove = null;
    private int humanScore = 0;
    private int computerScore = 0;
    private int tieScore = 0;
    private Player currentPlayer = null;

    //Default constructor
    public GameRules() { 

    }
    public GameRules(Player currentPlayer, int numOfRounds, int humanScore, int computerScore, int tieScore) {
        this.currentPlayer = currentPlayer;
        this.numOfRounds = numOfRounds;
        this.humanScore = humanScore;
        this.computerScore = computerScore;
        this.tieScore = tieScore;

    }
    //--------------------------------------------------------


    /*
    Main round mechanism    
    */
    public void playRound(Prediction prediction, Input input, UserManagement um, Gson gson, DataLoader dl) {
        int currRound = 1;
        while(currRound <= numOfRounds) {
            System.out.print("Round " + currRound + " - ");
            Move userMove = input.getInput();
            Move computerChoice = prediction.predictMove(currentPlayer, lastUserMove);
            score(userMove, computerChoice, currentPlayer);
            System.out.println();
            lastUserMove = userMove;
            currRound++;
        } 

        currentPlayer.setFavoriteMove();
        um.savePlayer(currentPlayer);
        Player[] finalPlayers = um.getPlayersAsArray();
        dl.store(gson, finalPlayers);
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
