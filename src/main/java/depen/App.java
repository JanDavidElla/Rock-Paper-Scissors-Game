package depen;

import depen.DataLoader;
import depen.Player;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;   
import java.util.ArrayList;


public class App {

    static int numOfRounds = 20;
    static int humanScore = 0;
    static int computerScore = 0;
    static int tieScore = 0;
    static Player currentPlayer = null;

    public static void main(String[] args) throws Exception {
        DataLoader dl = new DataLoader(".", "data.json");
        Gson gson = new Gson();
        Scanner scnr = new Scanner(System.in);
        Random rand = new Random();
        
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(dl.load(gson)));
        int currRound = 1;   

        System.out.println("Enter your username:");
        String userName = scnr.nextLine();
        
        for (Player player : players) {
            if (player.getUserName().equals(userName)) {
                System.out.println("Welcome back, " + player.getName() + "!");
                currentPlayer = player;
            }
        }
        

        while(currRound <= numOfRounds) {
            int computerChoice = rand.nextInt(3) + 1; //compares 1-3 for user input
            System.out.print("Round " + currRound + " - ");
            int userMove = getValidMove(scnr);
            playRound(userMove, computerChoice, currentPlayer);
            System.out.println();
            
            currRound++;

        }
        scnr.close();
        currentPlayer.setFavoriteMove();
        players.removeIf(player -> player.getUserName().equals(currentPlayer.getUserName()));
        players.add(currentPlayer);
        Player[] finalPlayers = players.toArray(new Player[0]);
        dl.store(gson, finalPlayers);
    }


    public static int getValidMove(Scanner scnr) {
        while(true) {
            System.out.print("Choose (1 = rock, 2 = paper, 3 = scissors): ");

            if(!scnr.hasNextInt()) {
                System.out.println("Invalid input. Try again.");
                scnr.next();
                continue;
            }

            int userMove = scnr.nextInt();
            if(userMove >= 1 && userMove <= 3) {
                return userMove;
            } else {
                System.out.println("Invalid input. Try again.");
            }

        }
    }

    public static void playRound(int userMove, int computerChoice, Player player) {
        if(userMove == computerChoice) {
            printScore(userMove, computerChoice, "Draw!"); 
            tieScore++;
        } else if((userMove == 1 && computerChoice == 3) || (userMove == 2 && computerChoice == 1) || (userMove == 3 && computerChoice == 2)) {
            printScore(userMove, computerChoice, "You win this round!");
            humanScore++;
            player.incrementWins();
        } else {
            printScore(userMove, computerChoice, "Computer wins this round!");
            computerScore++;
            player.incrementLosses();
        }
        player.addMoveToHistory(userMove == 1 ? 'R' : userMove == 2 ? 'P' : 'S');
    }

    public static String humanChoice(int userMove) {
        if(userMove == 1) {
            return "rock";
        } else if(userMove == 2) {
            return "paper";
        } else if(userMove == 3) {
            return "scissors";
        } else {
            return "Invalid move";
        }
    }

    public static String computerChoice(int computerChoice) {
        if(computerChoice == 1) {
            return "rock";
        } else if(computerChoice == 2) {
            return "paper";
        } else {
            return "scissors";
        }
    }

    public static void printScore(int userMove, int computerChoice, String win) {
        System.out.println("You chose " + humanChoice(userMove) + ". The computer chose " + computerChoice(computerChoice) + ". " + win);
        System.out.println("Score: Human: " + humanScore + " Computer: " + computerScore + " Draws: " + tieScore);
    }
}
