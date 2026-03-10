package depen;

import java.util.ArrayList;

public class Player {
    private String userName;
    private String name;
    private Integer wins;
    private Integer losses;
    private String favoriteMove;
    private ArrayList<Character> moveHistory;

    public Player(String userName, String name) {
        this.userName = userName;
        this.name = name;
        this.wins = 0;
        this.losses = 0;
        this.favoriteMove = null;
        this.moveHistory = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }
    public String getName() {
        return name;
    }

    public void incrementWins() {
        this.wins++;
    }
    public void incrementLosses() {
        this.losses++;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMoveToHistory(char move) {
        this.moveHistory.add(move)  ;
    }

    public void setFavoriteMove() {
        if (moveHistory.size() == 0) {
            this.favoriteMove = null;
            return;
        }
        int rockCount = 0, paperCount = 0, scissorsCount = 0;
        for (Character move : moveHistory) {
            if (move == 'R') rockCount++;
            else if (move == 'P') paperCount++;
            else if (move == 'S') scissorsCount++;
        }
        if (rockCount >= paperCount && rockCount >= scissorsCount) {
            this.favoriteMove = "Rock";
        } else if (paperCount >= rockCount && paperCount >= scissorsCount) {
            this.favoriteMove = "Paper";
        } else {
            this.favoriteMove = "Scissors";
        }
    }

    
}
