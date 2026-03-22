package depen;

public enum Move {
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors");

    private final String name;

    Move(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}