import java.util.Scanner;
import java.util.Random;

public class App {

    static int numOfRounds = 20;
    static int humanScore = 0;
    static int computerScore = 0;
    static int tieScore = 0;

    public static void main(String[] args) throws Exception {
        int currRound = 1;
        Scanner scnr = new Scanner(System.in);
        Random rand = new Random();

        while(currRound <= numOfRounds) {
            int computerChoice = rand.nextInt(3) + 1; //compares 1-3 for user input
            System.out.print("Round " + currRound + " - ");
            int userMove = getValidMove(scnr);
            playRound(userMove, computerChoice);
            System.out.println();
            
            currRound++;

        }
        scnr.close();
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

    public static void playRound(int userMove, int computerChoice) {
        if(userMove == computerChoice) {
            printScore(userMove, computerChoice, "Draw!"); 
            tieScore++;
        } else if((userMove == 1 && computerChoice == 3) || (userMove == 2 && computerChoice == 1) || (userMove == 3 && computerChoice == 2)) {
            printScore(userMove, computerChoice, "You win this round!");
            humanScore++;
        } else {
            printScore(userMove, computerChoice, "Computer wins this round!");
            computerScore++;
        }
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
