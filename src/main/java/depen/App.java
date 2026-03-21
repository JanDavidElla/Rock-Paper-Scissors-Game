package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class App {

    private static int numOfRounds = 20;
    private static int humanScore = 0;
    private static int computerScore = 0;
    private static int tieScore = 0;

    public static void main(String[] args) throws Exception {
        DataLoader dl = new DataLoader(".", "data.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Scanner scnr = new Scanner(System.in);
        Prediction prediction = new Prediction();
        Input input = new Input(scnr);

        ArrayList<Player> players = new ArrayList<>(Arrays.asList(dl.load(gson)));

        UserManagement um = new UserManagement(players);
        Player currentPlayer = um.login(scnr);

        GameRules game = new GameRules(currentPlayer, numOfRounds, humanScore, computerScore, tieScore);
        game.playRound(prediction, input, um, gson, dl);

    }
}