package depen;

import depen.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

public class DataLoader implements DataStorage {

    private String filePath;
    private String fileName;

    public DataLoader(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public boolean store(Gson gson, Player[] players) {
        try (Writer writer = new FileWriter(this.filePath + "/" + this.fileName)) {
            gson.toJson(players, writer);
        } catch (Exception e) {
            System.out.println("Error storing data: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Player[] load(Gson gson) {
        try(FileReader reader = new FileReader(this.filePath + "/" + this.fileName)) {
            Player[] players = gson.fromJson(reader, Player[].class);
            return players;
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            return null;
        }
    }

    public boolean load_example(){
        Player player = new Player("67", "Andrew");
        Player player1 = new Player("68", "Ron");
        Player player2 = new Player("69", "Jan");
    
        Player[] players = {player, player1, player2};
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(this.filePath + "/" + this.fileName)) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
