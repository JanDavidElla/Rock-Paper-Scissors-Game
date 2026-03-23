package depen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private Player player;

    @BeforeEach
    public void setUp() throws Exception {
        player = new Player("testUser", "Tester");
        resetStaticScores();
    }

    //Helper methods to reset static scores and access them for assertions

    private void resetStaticScores() throws Exception {
        for (String fieldName : new String[] { "humanScore", "computerScore", "tieScore" }) {
            Field f = App.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(null, 0);
        }
    }

    private int getScore(String fieldName) throws Exception {
        Field f = App.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (int) f.get(null);
    }

    //Tests for humanChoice() and computerChoice() methods

    @Test
    public void testHumanChoiceRock() {
        assertEquals("rock", App.humanChoice(Move.ROCK));
    }

    @Test
    public void testHumanChoicePaper() {
        assertEquals("paper", App.humanChoice(Move.PAPER));
    }

    @Test
    public void testHumanChoiceScissors() {
        assertEquals("scissors", App.humanChoice(Move.SCISSORS));
    }

    @Test
    public void testComputerChoiceRock() {
        assertEquals("rock", App.computerChoice(Move.ROCK));
    }

    @Test
    public void testComputerChoicePaper() {
        assertEquals("paper", App.computerChoice(Move.PAPER));
    }

    @Test
    public void testComputerChoiceScissors() {
        assertEquals("scissors", App.computerChoice(Move.SCISSORS));
    }

    //Tests for playRound() method

    // Tests for draw scenarios

    @Test
    public void testPlayRoundDrawRock() throws Exception {
        App.playRound(Move.ROCK, Move.ROCK, player);
        assertEquals(1, getScore("tieScore"));
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundDrawPaper() throws Exception {
        App.playRound(Move.PAPER, Move.PAPER, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawScissors() throws Exception {
        App.playRound(Move.SCISSORS, Move.SCISSORS, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawDoesNotIncrementPlayerStats() throws Exception {
        int winsBeforeSize = player.getMoveHistory().size();
        App.playRound(Move.ROCK, Move.ROCK, player);
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    // Tests for human win scenarios

    @Test
    public void testPlayRoundRockBeatsScissors() throws Exception {
        App.playRound(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperBeatsRock() throws Exception {
        App.playRound(Move.PAPER, Move.ROCK, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundScissorsBeatsPaper() throws Exception {
        App.playRound(Move.SCISSORS, Move.PAPER, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundHumanWinAddsToMoveHistory() throws Exception {
        App.playRound(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.ROCK, player.getMoveHistory().get(0));
    }

    // Tests for computer win scenarios

    @Test
    public void testPlayRoundScissorsLosesToRock() throws Exception {
        App.playRound(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, getScore("computerScore"));
        assertEquals(0, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundRockLosesToPaper() throws Exception {
        App.playRound(Move.ROCK, Move.PAPER, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperLosesToScissors() throws Exception {
        App.playRound(Move.PAPER, Move.SCISSORS, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundComputerWinAddsToMoveHistory() throws Exception {
        App.playRound(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.SCISSORS, player.getMoveHistory().get(0));
    }

    // Tests for score accumulation and move history growth over multiple rounds

    @Test
    public void testScoresAccumulateOverMultipleRounds() throws Exception {
        App.playRound(Move.ROCK, Move.SCISSORS, player); // human win
        App.playRound(Move.ROCK, Move.PAPER, player); // computer win
        App.playRound(Move.ROCK, Move.ROCK, player); // tie
        assertEquals(1, getScore("humanScore"));
        assertEquals(1, getScore("computerScore"));
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testMoveHistoryGrowsEachRound() throws Exception {
        App.playRound(Move.ROCK, Move.SCISSORS, player);
        App.playRound(Move.PAPER, Move.ROCK, player);
        App.playRound(Move.SCISSORS, Move.PAPER, player);
        assertEquals(3, player.getMoveHistory().size());
    }
}