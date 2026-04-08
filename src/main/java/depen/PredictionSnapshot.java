package depen;

public class PredictionSnapshot {
    private final Move predictedHumanMove;
    private final Move computerMove;
    private final Double confidence;

    public PredictionSnapshot(Move predictedHumanMove, Move computerMove, Double confidence) {
        this.predictedHumanMove = predictedHumanMove;
        this.computerMove = computerMove;
        this.confidence = confidence;
    }

    public Move getPredictedHumanMove() {
        return predictedHumanMove;
    }

    public Move getComputerMove() {
        return computerMove;
    }

    public Double getConfidence() {
        return confidence;
    }
}
