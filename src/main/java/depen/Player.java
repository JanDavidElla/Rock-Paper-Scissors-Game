package depen;

import java.util.ArrayList;

public class Player {
    private String userName;
    private String name;
    private Integer wins;
    private Integer losses;
    private String favoriteMove;
    private ArrayList<Move> moveHistory;

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

    public String getFavoriteMove() {
        return favoriteMove;
    }

    public ArrayList<Move> getMoveHistory() {
        return moveHistory;
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

    public void addMoveToHistory(Move move) {
        this.moveHistory.add(move);
    }

    public void setFavoriteMove() {
        if (moveHistory.size() == 0) {
            this.favoriteMove = null;
            return;
        }

        int rockCount = 0;
        int paperCount = 0;
        int scissorsCount = 0;

        for (Move move : moveHistory) {
            if (move == Move.ROCK) rockCount++;
            else if (move == Move.PAPER) paperCount++;
            else if (move == Move.SCISSORS) scissorsCount++;
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
