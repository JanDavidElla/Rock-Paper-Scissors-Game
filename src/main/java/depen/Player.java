package depen;

public class Player {
    int playerId;
    String name;
    Integer wins;
    Integer losses;
    String favoriteMove;
    Character[] moveHistory;

    public Player(int id) {
        this.playerId = id;
        // this.name = name;
        // this.wins = 0;
        // this.losses = 0;
        // this.favoriteMove = null;
        // this.moveHistory = new Character[0];
    }

    public int getPlayerId() {
        return playerId;
    }
    // public String getName() {
    //     return name;
    // }

    // public void setWins(Integer wins) {
    //     this.wins = wins;
    // }
    // public void setLosses(Integer losses) {
    //     this.losses = losses;
    // }
    // public void setName(String name) {
    //     this.name = name;
    // }

    
}
