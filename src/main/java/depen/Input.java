package depen;

import java.util.Scanner;

public class Input {
    private Scanner scnr;


    public Input(Scanner scnr) {
        this.scnr = scnr;
    }

    public Move getInput() {
        while(true) {
            System.out.print("Choose (1 = rock, 2 = paper, 3 = scissors): ");

            if(!scnr.hasNext()) {
                System.out.println("Invalid input. Try again.");
                scnr.next();
                continue;
            }

            int userMove = scnr.nextInt();
            if(userMove >= 1 && userMove <= 3) {
                return Move.values()[userMove - 1];
            } else {
                System.out.println("Invalid input. Try again.");
            }
            
            
        }
    }
}

