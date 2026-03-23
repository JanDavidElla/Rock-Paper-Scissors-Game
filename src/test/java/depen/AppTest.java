package depen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private Player player;
    private GameRules gameRules;

    @BeforeEach
    public void setUp() throws Exception {
        player = new Player("testUser", "Tester");
        gameRules = new GameRules(player, 20, 0, 0, 0);
    }

    // Helper methods to access GameRules instance scores

    private int getScore(String fieldName) throws Exception {
        Field f = GameRules.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (int) f.get(gameRules);
    }

    // Tests for humanChoice() and computerChoice() methods

    @Test
    public void testHumanChoiceRock() {
        assertEquals("rock", gameRules.humanChoice(Move.ROCK));
    }

    @Test
    public void testHumanChoicePaper() {
        assertEquals("paper", gameRules.humanChoice(Move.PAPER));
    }

    @Test
    public void testHumanChoiceScissors() {
        assertEquals("scissors", gameRules.humanChoice(Move.SCISSORS));
    }

    @Test
    public void testComputerChoiceRock() {
        assertEquals("rock", gameRules.computerChoice(Move.ROCK));
    }

    @Test
    public void testComputerChoicePaper() {
        assertEquals("paper", gameRules.computerChoice(Move.PAPER));
    }

    @Test
    public void testComputerChoiceScissors() {
        assertEquals("scissors", gameRules.computerChoice(Move.SCISSORS));
    }

    // Tests for score() method

    // Tests for draw scenarios

    @Test
    public void testPlayRoundDrawRock() throws Exception {
        gameRules.score(Move.ROCK, Move.ROCK, player);
        assertEquals(1, getScore("tieScore"));
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundDrawPaper() throws Exception {
        gameRules.score(Move.PAPER, Move.PAPER, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawScissors() throws Exception {
        gameRules.score(Move.SCISSORS, Move.SCISSORS, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawDoesNotIncrementPlayerStats() throws Exception {
        int winsBeforeSize = player.getMoveHistory().size();
        gameRules.score(Move.ROCK, Move.ROCK, player);
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    // Tests for human win scenarios

    @Test
    public void testPlayRoundRockBeatsScissors() throws Exception {
        gameRules.score(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperBeatsRock() throws Exception {
        gameRules.score(Move.PAPER, Move.ROCK, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundScissorsBeatsPaper() throws Exception {
        gameRules.score(Move.SCISSORS, Move.PAPER, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundHumanWinAddsToMoveHistory() throws Exception {
        gameRules.score(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.ROCK, player.getMoveHistory().get(0));
    }

    // Tests for computer win scenarios

    @Test
    public void testPlayRoundScissorsLosesToRock() throws Exception {
        gameRules.score(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, getScore("computerScore"));
        assertEquals(0, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundRockLosesToPaper() throws Exception {
        gameRules.score(Move.ROCK, Move.PAPER, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperLosesToScissors() throws Exception {
        gameRules.score(Move.PAPER, Move.SCISSORS, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundComputerWinAddsToMoveHistory() throws Exception {
        gameRules.score(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.SCISSORS, player.getMoveHistory().get(0));
    }

    // Tests for score accumulation and move history growth over multiple rounds

    @Test
    public void testScoresAccumulateOverMultipleRounds() throws Exception {
        gameRules.score(Move.ROCK, Move.SCISSORS, player); // human win
        gameRules.score(Move.ROCK, Move.PAPER, player); // computer win
        gameRules.score(Move.ROCK, Move.ROCK, player); // tie
        assertEquals(1, getScore("humanScore"));
        assertEquals(1, getScore("computerScore"));
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testMoveHistoryGrowsEachRound() throws Exception {
        gameRules.score(Move.ROCK, Move.SCISSORS, player);
        gameRules.score(Move.PAPER, Move.ROCK, player);
        gameRules.score(Move.SCISSORS, Move.PAPER, player);
        assertEquals(3, player.getMoveHistory().size());
    }
}