package depen;

import com.google.gson.Gson;

public class GameSession {

    private final Player currentPlayer;
    private final int totalRounds;
    private final ChoiceStrategy strategy;
    private final GameRules gameRules;
    private final UserManagement userManagement;
    private final DataLoader dataLoader;
    private final Gson gson;

    private int currentRound;
    private int humanWins;
    private int computerWins;
    private int ties;
    private Move lastHumanMove;
    private boolean gameOver;

    public GameSession(
            Player currentPlayer,
            int totalRounds,
            ChoiceStrategy strategy,
            GameRules gameRules,
            UserManagement userManagement,
            DataLoader dataLoader,
            Gson gson) {
        this.currentPlayer = currentPlayer;
        this.totalRounds = totalRounds;
        this.strategy = strategy;
        this.gameRules = gameRules;
        this.userManagement = userManagement;
        this.dataLoader = dataLoader;
        this.gson = gson;
        this.currentRound = 1;
    }

    public RoundResult playRound(Move humanMove) {
        if (gameOver) {
            throw new IllegalStateException("The game is already over.");
        }

        PredictionSnapshot snapshot = strategy.getPredictionSnapshot(currentPlayer, lastHumanMove);
        Move computerMove = snapshot.getComputerMove();
        RoundOutcome outcome = gameRules.determineOutcome(humanMove, computerMove);

        updateScores(outcome);
        updatePlayerHistory(humanMove, outcome);
        strategy.onRoundEnd(humanMove, computerMove);

        RoundResult result = new RoundResult(
                currentRound,
                totalRounds,
                humanMove,
                snapshot.getPredictedHumanMove(),
                computerMove,
                snapshot.getConfidence(),
                strategy.getStrategyName(),
                outcome,
                humanWins,
                computerWins,
                ties,
                currentRound >= totalRounds);

        lastHumanMove = humanMove;

        if (currentRound >= totalRounds) {
            gameOver = true;
            persistSessionState();
        } else {
            currentRound++;
        }

        return result;
    }

    public void closeSession() {
        persistSessionState();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
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

    public String getStrategyName() {
        return strategy.getStrategyName();
    }

    private void updateScores(RoundOutcome outcome) {
        switch (outcome) {
            case HUMAN_WIN -> humanWins++;
            case COMPUTER_WIN -> computerWins++;
            case TIE -> ties++;
        }
    }

    private void updatePlayerHistory(Move humanMove, RoundOutcome outcome) {
        currentPlayer.addMoveToHistory(humanMove);

        switch (outcome) {
            case HUMAN_WIN -> currentPlayer.incrementWins();
            case COMPUTER_WIN -> currentPlayer.incrementLosses();
            case TIE -> {
            }
        }
    }

    private void persistSessionState() {
        currentPlayer.setFavoriteMove();
        userManagement.savePlayer(currentPlayer);
        dataLoader.store(gson, userManagement.getPlayersAsArray());
        strategy.onGameEnd();
    }
}
