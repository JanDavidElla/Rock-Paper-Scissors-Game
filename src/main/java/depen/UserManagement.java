package depen;

import java.util.ArrayList;
import java.util.Scanner;

public class UserManagement {

    private ArrayList<Player> players;

    public UserManagement(ArrayList<Player> players) {
        this.players = players;
    }

    public Player login(Scanner scnr) {
        System.out.print("Enter your username (press enter for new account): ");
        String userName = scnr.nextLine().trim();

        Player found = findByUsername(userName);

        if (found != null) {
            System.out.println("Welcome back, " + found.getName() + "!");
            if (found.getFavoriteMove() != null) {
                System.out.println("Your favorite move is " + found.getFavoriteMove() + "!");
            }
            return found;
        }

        System.out.println("No account found for \"" + userName + "\".");
        System.out.print("Create a new account? (y/n): ");
        String choice = scnr.nextLine().trim().toLowerCase();

        if (!choice.equals("y")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        String newUserName;
        while (true) {
            System.out.print("Choose a username: ");
            newUserName = scnr.nextLine().trim();
            if (newUserName.isEmpty()) {
                System.out.println("Username cannot be empty. Try again.");
            } else if (findByUsername(newUserName) != null) {
                System.out.println("Username \"" + newUserName + "\" is already taken. Try another.");
            } else {
                break;
            }
        }

        String name;
        while (true) {
            System.out.print("Enter your display name: ");
            name = scnr.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Display name cannot be empty. Try again.");
            } else {
                break;
            }
        }

        Player newPlayer = new Player(newUserName, name);
        players.add(newPlayer);
        System.out.println("Account created! Welcome, " + name + "!");
        return newPlayer;
    }

    public void savePlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUserName().equals(player.getUserName())) {
            players.set(i, player);
            return;
            }
        }
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player[] getPlayersAsArray() {
        return players.toArray(new Player[0]);
    }

    private Player findByUsername(String userName) {
        for (Player p : players) {
            if (p.getUserName().equals(userName)) {
                return p;
            }
        }
        return null;
    }
}