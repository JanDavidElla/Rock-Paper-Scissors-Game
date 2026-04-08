package depen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class PredictionModel {
    private static final int SEQUENCE_LENGTH = 5;
    private static final int MAX_HISTORY_SIZE = 100;

    private final Map<String, Integer> sequenceCounts;
    private final Deque<Character> recentChoices;

    PredictionModel(State state) {
        this.sequenceCounts = new HashMap<>();
        this.recentChoices = new ArrayDeque<>();

        if (state.sequenceCounts != null) {
            this.sequenceCounts.putAll(state.sequenceCounts);
        }

        if (state.recentChoices != null) {
            for (Character symbol : state.recentChoices) {
                this.recentChoices.addLast(symbol);
            }
        }
    }

    PredictionDetail predictHumanMove() {
        if (recentChoices.size() < SEQUENCE_LENGTH - 1) {
            return new PredictionDetail(null, null);
        }

        String prefix = getLastChoices(SEQUENCE_LENGTH - 1);
        int rockFreq = sequenceCounts.getOrDefault(prefix + "R", 0);
        int paperFreq = sequenceCounts.getOrDefault(prefix + "P", 0);
        int scissorsFreq = sequenceCounts.getOrDefault(prefix + "S", 0);
        int total = rockFreq + paperFreq + scissorsFreq;

        if (total == 0) {
            return new PredictionDetail(null, null);
        }

        Move predictedMove;
        int strongestCount;

        if (rockFreq >= paperFreq && rockFreq >= scissorsFreq) {
            predictedMove = Move.ROCK;
            strongestCount = rockFreq;
        } else if (paperFreq >= rockFreq && paperFreq >= scissorsFreq) {
            predictedMove = Move.PAPER;
            strongestCount = paperFreq;
        } else {
            predictedMove = Move.SCISSORS;
            strongestCount = scissorsFreq;
        }

        return new PredictionDetail(predictedMove, strongestCount / (double) total);
    }

    void recordRound(Move humanMove, Move computerMove) {
        addChoice(encodeHumanMove(humanMove), true);
        addChoice(encodeComputerMove(computerMove), false);
    }

    State snapshotState() {
        State state = new State();
        state.sequenceCounts = new HashMap<>(sequenceCounts);
        state.recentChoices = new ArrayList<>(recentChoices);
        return state;
    }

    private void addChoice(char symbol, boolean storeIfReady) {
        recentChoices.addLast(symbol);

        while (recentChoices.size() > MAX_HISTORY_SIZE) {
            recentChoices.removeFirst();
        }

        if (storeIfReady && recentChoices.size() >= SEQUENCE_LENGTH) {
            String sequence = getLastChoices(SEQUENCE_LENGTH);
            sequenceCounts.put(sequence, sequenceCounts.getOrDefault(sequence, 0) + 1);
        }
    }

    private String getLastChoices(int count) {
        ArrayList<Character> chars = new ArrayList<>(recentChoices);
        StringBuilder builder = new StringBuilder();

        for (int i = chars.size() - count; i < chars.size(); i++) {
            builder.append(chars.get(i));
        }

        return builder.toString();
    }

    private char encodeHumanMove(Move move) {
        return switch (move) {
            case ROCK -> 'R';
            case PAPER -> 'P';
            case SCISSORS -> 'S';
        };
    }

    private char encodeComputerMove(Move move) {
        return switch (move) {
            case ROCK -> 'r';
            case PAPER -> 'p';
            case SCISSORS -> 's';
        };
    }

    static final class State {
        Map<String, Integer> sequenceCounts = new HashMap<>();
        ArrayList<Character> recentChoices = new ArrayList<>();
    }

    static final class PredictionDetail {
        private final Move predictedMove;
        private final Double confidence;

        PredictionDetail(Move predictedMove, Double confidence) {
            this.predictedMove = predictedMove;
            this.confidence = confidence;
        }

        Move predictedMove() {
            return predictedMove;
        }

        Double confidence() {
            return confidence;
        }
    }
}
