package depen;

import java.util.Scanner;

public class Input {
    private final Scanner scnr;

    public Input(Scanner scnr) {
        this.scnr = scnr;
    }

    public Move playerMove() {
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

    public ChoiceStrategy chooseComputerStrategy() {
        while (true) {
            System.out.print("Choose strategy: R (random) or M (machine learning?): ");
            String s = scnr.next();
            if(s.equalsIgnoreCase("r")) {
                return new RandomStrategy();
            } else if (s.equalsIgnoreCase("m")) {
                return new Prediction();
            } else {
                System.out.println("Invalid input, try again.");
            }

        }
    }
}