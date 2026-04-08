package depen;

public class RoundResult {
    private final int roundNumber;
    private final int totalRounds;
    private final Move humanMove;
    private final Move predictedHumanMove;
    private final Move computerMove;
    private final Double predictionConfidence;
    private final String strategyName;
    private final RoundOutcome outcome;
    private final int humanWins;
    private final int computerWins;
    private final int ties;
    private final boolean gameOver;

    public RoundResult(int roundNumber,
                       int totalRounds,
                       Move humanMove,
                       Move predictedHumanMove,
                       Move computerMove,
                       Double predictionConfidence,
                       String strategyName,
                       RoundOutcome outcome,
                       int humanWins,
                       int computerWins,
                       int ties,
                       boolean gameOver) {
        this.roundNumber = roundNumber;
        this.totalRounds = totalRounds;
        this.humanMove = humanMove;
        this.predictedHumanMove = predictedHumanMove;
        this.computerMove = computerMove;
        this.predictionConfidence = predictionConfidence;
        this.strategyName = strategyName;
        this.outcome = outcome;
        this.humanWins = humanWins;
        this.computerWins = computerWins;
        this.ties = ties;
        this.gameOver = gameOver;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public Move getHumanMove() {
        return humanMove;
    }

    public Move getPredictedHumanMove() {
        return predictedHumanMove;
    }

    public Move getComputerMove() {
        return computerMove;
    }

    public Double getPredictionConfidence() {
        return predictionConfidence;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public RoundOutcome getOutcome() {
        return outcome;
    }

    public int getHumanWins() {
        return humanWins;
    }

    public int getComputerWins() {
        return computerWins;
    }

    public int getTies() {
        return ties;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
