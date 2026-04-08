package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private Player player;
    private GameRules gameRules;

    @BeforeEach
    public void setUp() {
        player = new Player("testUser", "Tester");
        gameRules = new GameRules();
    }

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

    @Test
    public void testDetermineOutcomeTie() {
        assertEquals(RoundOutcome.TIE, gameRules.determineOutcome(Move.ROCK, Move.ROCK));
    }

    @Test
    public void testDetermineOutcomeHumanWin() {
        assertEquals(RoundOutcome.HUMAN_WIN, gameRules.determineOutcome(Move.PAPER, Move.ROCK));
    }

    @Test
    public void testDetermineOutcomeComputerWin() {
        assertEquals(RoundOutcome.COMPUTER_WIN, gameRules.determineOutcome(Move.SCISSORS, Move.ROCK));
    }

    @Test
    public void testGetRoundMessage() {
        assertEquals("Human wins this round!", gameRules.getRoundMessage(RoundOutcome.HUMAN_WIN));
    }

    @Test
    public void testGameSessionTracksScoresAndPersistsData() throws Exception {
        Path tempDir = Files.createTempDirectory("rps-app-test");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DataLoader dataLoader = new DataLoader(tempDir.toString(), "players.json");
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        UserManagement userManagement = new UserManagement(players);

        ChoiceStrategy strategy = new StubStrategy(
                new PredictionSnapshot(Move.ROCK, Move.SCISSORS, 0.75),
                new PredictionSnapshot(Move.PAPER, Move.ROCK, 0.60));

        GameSession session = new GameSession(player, 2, strategy, gameRules, userManagement, dataLoader, gson);

        RoundResult roundOne = session.playRound(Move.ROCK);
        assertEquals(1, roundOne.getRoundNumber());
        assertEquals(RoundOutcome.HUMAN_WIN, roundOne.getOutcome());
        assertEquals(1, roundOne.getHumanWins());
        assertEquals(0.75, roundOne.getPredictionConfidence());
        assertFalse(roundOne.isGameOver());

        RoundResult roundTwo = session.playRound(Move.PAPER);
        assertEquals(2, roundTwo.getRoundNumber());
        assertEquals(RoundOutcome.HUMAN_WIN, roundTwo.getOutcome());
        assertEquals(2, roundTwo.getHumanWins());
        assertTrue(roundTwo.isGameOver());

        Player[] savedPlayers = dataLoader.load(gson);
        assertEquals(1, savedPlayers.length);
        assertEquals(2, savedPlayers[0].getMoveHistory().size());
        assertEquals("Rock", savedPlayers[0].getFavoriteMove());
    }

    private static class StubStrategy implements ChoiceStrategy {
        private final PredictionSnapshot[] snapshots;
        private int index;

        private StubStrategy(PredictionSnapshot... snapshots) {
            this.snapshots = snapshots;
        }

        @Override
        public Move chooseMove(Player player, Move lastUserMove) {
            return getPredictionSnapshot(player, lastUserMove).getComputerMove();
        }

        @Override
        public PredictionSnapshot getPredictionSnapshot(Player player, Move lastUserMove) {
            PredictionSnapshot snapshot = snapshots[Math.min(index, snapshots.length - 1)];
            index++;
            return snapshot;
        }

        @Override
        public String getStrategyName() {
            return "Stub";
        }
    }
}
