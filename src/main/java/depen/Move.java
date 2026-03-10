package depen;

public enum Move {
    ROCK(1, "rock"), 
    PAPER(2, "paper"), 
    SCISSORS(3, "scissors");

    private final int value;
    private final String name;

    Move(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
