package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Prediction implements ChoiceStrategy {
    private static final int N = 5;
    private static final String ML_FILE = "ml-data.json";

    private final Random random = new Random();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Stores sequence frequencies, e.g. "RSPSS" -> 4
    private Map<String, Integer> sequenceCounts;

    // Rolling history of choice symbols across rounds.
    // Stores the full stream of choices; only the last N-1 are needed for prediction,
    // but storing a longer stream is fine.
    private Deque<Character> recentChoices;

    public Prediction() {
        MLState state = loadState();
        this.sequenceCounts = state.sequenceCounts == null ? new HashMap<>() : state.sequenceCounts;
        this.recentChoices = new ArrayDeque<>();

        if (state.recentChoices != null) {
            for (Character c : state.recentChoices) {
                this.recentChoices.addLast(c);
            }
        }
    }

    @Override
    public Move chooseMove(Player player, Move lastUserMove) {
        // Need at least N-1 previous choices to predict
        if (recentChoices.size() < N - 1) {
            return getRandomMove();
        }

        String prefix = getLastChoices(N - 1);

        int rockFreq = sequenceCounts.getOrDefault(prefix + "R", 0);
        int paperFreq = sequenceCounts.getOrDefault(prefix + "P", 0);
        int scissorsFreq = sequenceCounts.getOrDefault(prefix + "S", 0);

        if (rockFreq == 0 && paperFreq == 0 && scissorsFreq == 0) {
            return getRandomMove();
        }

        // Predict the human move with highest frequency, then counter it
        if (rockFreq >= paperFreq && rockFreq >= scissorsFreq) {
            return Move.PAPER;      // beats ROCK
        } else if (paperFreq >= rockFreq && paperFreq >= scissorsFreq) {
            return Move.SCISSORS;   // beats PAPER
        } else {
            return Move.ROCK;       // beats SCISSORS
        }
    }

    @Override
    public String getStrategyName() {
        return "Machine Learning";
    }

    @Override
    public void onRoundEnd(Move humanMove, Move computerMove) {
        // The assignment's sequence logic is based on the stream of choices between
        // human and computer. For each round, record human then computer.

        // 1) Append the human choice. If we now have at least N symbols, store the
        //    last N sequence. This produces sequences that end with a human move,
        //    which matches the PDF example used for prediction.
        addChoice(encodeHumanMove(humanMove), true);

        // 2) Append the computer choice. This becomes part of the prefix used for
        //    predicting the next human choice in the next round.
        addChoice(encodeComputerMove(computerMove), false);
    }

    @Override
    public void onGameEnd() {
        saveState();
    }

    private void addChoice(char symbol, boolean storeIfReady) {
        recentChoices.addLast(symbol);

        // Keep rolling history from growing forever; enough to preserve context
        // across games. Keeping the last 100 symbols is plenty.
        while (recentChoices.size() > 100) {
            recentChoices.removeFirst();
        }

        if (storeIfReady && recentChoices.size() >= N) {
            String sequence = getLastChoices(N);
            sequenceCounts.put(sequence, sequenceCounts.getOrDefault(sequence, 0) + 1);
        }
    }

    private String getLastChoices(int count) {
        ArrayList<Character> chars = new ArrayList<>(recentChoices);
        StringBuilder sb = new StringBuilder();

        for (int i = chars.size() - count; i < chars.size(); i++) {
            sb.append(chars.get(i));
        }

        return sb.toString();
    }

    private char encodeHumanMove(Move move) {
        return switch (move) {
            case ROCK -> 'R';
            case PAPER -> 'P';
            case SCISSORS -> 'S';
        };
    }

    // Lowercase for computer to distinguish human vs computer choices in the stream
    private char encodeComputerMove(Move move) {
        return switch (move) {
            case ROCK -> 'r';
            case PAPER -> 'p';
            case SCISSORS -> 's';
        };
    }

    private Move getRandomMove() {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }

    private MLState loadState() {
        try (FileReader reader = new FileReader(ML_FILE)) {
            MLState state = gson.fromJson(reader, MLState.class);
            return state == null ? new MLState() : state;
        } catch (Exception e) {
            return new MLState();
        }
    }

    private void saveState() {
        try (Writer writer = new FileWriter(ML_FILE)) {
            MLState state = new MLState();
            state.sequenceCounts = sequenceCounts;
            state.recentChoices = new ArrayList<>(recentChoices);
            gson.toJson(state, writer);
        } catch (Exception e) {
            System.out.println("Error saving ML data: " + e.getMessage());
        }
    }

    private static class MLState {
        Map<String, Integer> sequenceCounts = new HashMap<>();
        ArrayList<Character> recentChoices = new ArrayList<>();
    }
}