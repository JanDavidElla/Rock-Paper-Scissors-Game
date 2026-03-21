package depen;

import java.util.Scanner;

public class Input {
    private final Scanner scnr;

    public Input(Scanner scnr) {
        this.scnr = scnr;
    }

    public Move getInput() {
        while (true) {
            System.out.print("Choose (1 = rock, 2 = paper, 3 = scissors): ");

            if (!scnr.hasNextInt()) {
                System.out.println("Invalid input. Try again.");
                scnr.next();
                continue;
            }

            int userMove = scnr.nextInt();

            if (userMove >= 1 && userMove <= 3) {
                return Move.values()[userMove - 1];
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    public int getAlgorithmChoice() {
        while (true) {
            System.out.println("Choose computer algorithm:");
            System.out.println("1 - Random");
            System.out.println("2 - Machine Learning");
            System.out.print("Enter choice: ");

            if (!scnr.hasNextInt()) {
                System.out.println("Invalid input. Try again.");
                scnr.next();
                continue;
            }

            int choice = scnr.nextInt();

            if (choice == 1 || choice == 2) {
                return choice;
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }
    }
}