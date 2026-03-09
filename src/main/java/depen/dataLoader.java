package depen;

import depen.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class dataLoader {

    public static void main(String[] args) {
        
        Player player = new Player(67);
        Player player1 = new Player(68);
        Player player2 = new Player(69);

        Player[] players = {player, player1, player2};

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter("data.json")) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // String filePath = "output.json";
        // try (FileWriter file = new FileWriter(filePath)) {
        //     System.out.println("Successfully wrote JSON object to file: " + filePath);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
