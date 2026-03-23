package depen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private Player player;
    private GameRules game;

    @BeforeEach
    public void setUp() {
        player = new Player("testUser", "Tester");
        game = new GameRules(player, 20, 0, 0, 0);
    }

    private int getScore(String fieldName) throws Exception {
        Field f = GameRules.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (int) f.get(game);
    }

    //Tests for humanChoice() and computerChoice() methods

    @Test
    public void testHumanChoiceRock() {
        assertEquals("rock", game.humanChoice(Move.ROCK));
    }

    @Test
    public void testHumanChoicePaper() {
        assertEquals("paper", game.humanChoice(Move.PAPER));
    }

    @Test
    public void testHumanChoiceScissors() {
        assertEquals("scissors", game.humanChoice(Move.SCISSORS));
    }

    @Test
    public void testComputerChoiceRock() {
        assertEquals("rock", game.computerChoice(Move.ROCK));
    }

    @Test
    public void testComputerChoicePaper() {
        assertEquals("paper", game.computerChoice(Move.PAPER));
    }

    @Test
    public void testComputerChoiceScissors() {
        assertEquals("scissors", game.computerChoice(Move.SCISSORS));
    }

    //Tests for score() method

    // Tests for draw scenarios

    @Test
    public void testPlayRoundDrawRock() throws Exception {
        game.score(Move.ROCK, Move.ROCK, player);
        assertEquals(1, getScore("tieScore"));
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundDrawPaper() throws Exception {
        game.score(Move.PAPER, Move.PAPER, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawScissors() throws Exception {
        game.score(Move.SCISSORS, Move.SCISSORS, player);
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testPlayRoundDrawDoesNotIncrementPlayerStats() throws Exception {
        game.score(Move.ROCK, Move.ROCK, player);
        assertEquals(0, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    // Tests for human win scenarios

    @Test
    public void testPlayRoundRockBeatsScissors() throws Exception {
        game.score(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, getScore("humanScore"));
        assertEquals(0, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperBeatsRock() throws Exception {
        game.score(Move.PAPER, Move.ROCK, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundScissorsBeatsPaper() throws Exception {
        game.score(Move.SCISSORS, Move.PAPER, player);
        assertEquals(1, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundHumanWinAddsToMoveHistory() throws Exception {
        game.score(Move.ROCK, Move.SCISSORS, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.ROCK, player.getMoveHistory().get(0));
    }

    // Tests for computer win scenarios

    @Test
    public void testPlayRoundScissorsLosesToRock() throws Exception {
        game.score(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, getScore("computerScore"));
        assertEquals(0, getScore("humanScore"));
    }

    @Test
    public void testPlayRoundRockLosesToPaper() throws Exception {
        game.score(Move.ROCK, Move.PAPER, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundPaperLosesToScissors() throws Exception {
        game.score(Move.PAPER, Move.SCISSORS, player);
        assertEquals(1, getScore("computerScore"));
    }

    @Test
    public void testPlayRoundComputerWinAddsToMoveHistory() throws Exception {
        game.score(Move.SCISSORS, Move.ROCK, player);
        assertEquals(1, player.getMoveHistory().size());
        assertEquals(Move.SCISSORS, player.getMoveHistory().get(0));
    }

    // Tests for score accumulation and move history growth over multiple rounds

    @Test
    public void testScoresAccumulateOverMultipleRounds() throws Exception {
        game.score(Move.ROCK, Move.SCISSORS, player); // human win
        game.score(Move.ROCK, Move.PAPER, player); // computer win
        game.score(Move.ROCK, Move.ROCK, player); // tie
        assertEquals(1, getScore("humanScore"));
        assertEquals(1, getScore("computerScore"));
        assertEquals(1, getScore("tieScore"));
    }

    @Test
    public void testMoveHistoryGrowsEachRound() throws Exception {
        game.score(Move.ROCK, Move.SCISSORS, player);
        game.score(Move.PAPER, Move.ROCK, player);
        game.score(Move.SCISSORS, Move.PAPER, player);
        assertEquals(3, player.getMoveHistory().size());
    }
}
