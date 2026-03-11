package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;


public class App {

    private static int numOfRounds = 20;
    private static Move lastUserMove = null;
    private static int humanScore = 0;
    private static int computerScore = 0;
    private static int tieScore = 0;
    private static Player currentPlayer = null;

    public static void main(String[] args) throws Exception {
        DataLoader dl = new DataLoader(".", "data.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Scanner scnr = new Scanner(System.in);
        Prediction prediction = new Prediction();
        
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(dl.load(gson)));
        int currRound = 1;

        UserManagement um = new UserManagement(players);
        currentPlayer = um.login(scnr);
        
        /*
        What happens each round
        */
        while(currRound <= numOfRounds) { 
            System.out.print("Round " + currRound + " - ");
            Move userMove = getValidMove(scnr);
            Move computerChoice = prediction.predictMove(currentPlayer, lastUserMove);
            playRound(userMove, computerChoice, currentPlayer);
            System.out.println();
            lastUserMove = userMove;
            currRound++;

        }
        scnr.close();
        currentPlayer.setFavoriteMove(); 
        um.savePlayer(currentPlayer); //Saves player for future plays
        Player[] finalPlayers = um.getPlayersAsArray();
        dl.store(gson, finalPlayers); 
    }

//This is the main valid move function
//Change THIS if you want to change how moves are interpreted (Strings, integers, etc.)

    public static Move getValidMove(Scanner scnr) { 
        while(true) { //Asks user again if given invalid input.
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

    public static void playRound(Move userMove, Move computerChoice, Player player) { //Main game mechanism
        if(userMove == computerChoice) {
            printScore(userMove, computerChoice, "Draw!"); 
            tieScore++;
        } else if((userMove == Move.ROCK && computerChoice == Move.SCISSORS) || (userMove == Move.PAPER && computerChoice == Move.ROCK) || (userMove == Move.SCISSORS && computerChoice == Move.PAPER)) {
            printScore(userMove, computerChoice, "You win this round!");
            humanScore++;
            player.incrementWins();
        } else {
            printScore(userMove, computerChoice, "Computer wins this round!");
            computerScore++;
            player.incrementLosses();
        }
        player.addMoveToHistory(userMove);
    }

    public static String humanChoice(Move userMove) {
        return userMove.getName();
    }

    public static String computerChoice(Move computerChoice) {
        return computerChoice.getName();
    }

    public static void printScore(Move userMove, Move computerChoice, String win) {
        System.out.println("You chose " + humanChoice(userMove) + ". The computer chose " + computerChoice(computerChoice) + ". " + win);
        System.out.println("Score: Human: " + humanScore + " Computer: " + computerScore + " Draws: " + tieScore);
    }
}
