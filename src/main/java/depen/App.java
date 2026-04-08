package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class App extends Application {
    private static final int DEFAULT_ROUNDS = 20;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final DataLoader dataLoader = new DataLoader(".", "data.json");
    private final GameRules gameRules = new GameRules();
    private final ObservableList<String> roundHistory = FXCollections.observableArrayList();

    private UserManagement userManagement;
    private Player currentPlayer;
    private GameSession gameSession;
    private Stage primaryStage;
    private AppView view;
    private boolean intelligentMode = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(dataLoader.load(gson)));
        userManagement = new UserManagement(players);

        primaryStage = stage;
        view = new AppView(
                roundHistory,
                this::playRound,
                this::handleNewGame,
                Platform::exit,
                this::showAbout,
                this::updateStrategySelection);

        stage.setTitle("CS 151 Assignment 5 - Rock Paper Scissors");
        stage.setScene(new Scene(view.getRoot(), 850, 900));
        stage.setMinWidth(800);
        stage.setMinHeight(900);
        view.setMoveButtonsDisabled(true);
        showWaitingState();
        stage.show();

        Platform.runLater(this::finishStartup);
    }

    @Override
    public void stop() {
        if (gameSession != null) {
            gameSession.closeSession();
            return;
        }

        if (currentPlayer != null) {
            currentPlayer.setFavoriteMove();
            userManagement.savePlayer(currentPlayer);
            dataLoader.store(gson, userManagement.getPlayersAsArray());
        }
    }

    private void finishStartup() {
        currentPlayer = promptForPlayer();
        if (currentPlayer == null) {
            Platform.exit();
            return;
        }

        startNewGame(DEFAULT_ROUNDS);
    }

    private void handleNewGame() {
        Integer rounds = getRequestedRoundCount();
        if (rounds != null && currentPlayer != null) {
            startNewGame(rounds);
        }
    }

    private void startNewGame(int rounds) {
        if (gameSession != null) {
            gameSession.closeSession();
        }

        ChoiceStrategy strategy = StrategyFactory.create(intelligentMode);
        gameSession = new GameSession(currentPlayer, rounds, strategy, gameRules, userManagement, dataLoader, gson);

        roundHistory.clear();
        roundHistory.add("New game: " + rounds + " rounds, " + gameSession.getStrategyName());
        view.setMoveButtonsDisabled(false);

        view.playerLabel.setText(buildPlayerLabel());
        view.strategyLabel.setText("Current strategy: " + gameSession.getStrategyName());
        view.roundLabel.setText(buildRoundLabel());
        view.predictionLabel.setText(buildStartingPredictionText());
        view.computerMoveLabel.setText("Computer move: waiting");
        view.resultLabel.setText("Round result: waiting for move");
        view.gameStatusLabel.setText("Game in progress.");
        view.humanWinsLabel.setText("Human wins: 0");
        view.computerWinsLabel.setText("Computer wins: 0");
        view.tiesLabel.setText("Ties: 0");
        updateStrategySelection();
    }

    private void playRound(Move move) {
        RoundResult result = gameSession.playRound(move);

        roundHistory.add(buildHistoryEntry(result));
        view.historyListView.scrollTo(roundHistory.size() - 1);

        view.playerLabel.setText(buildPlayerLabel());
        view.strategyLabel.setText("Current strategy: " + result.getStrategyName());
        view.roundLabel.setText(buildRoundLabel());
        view.predictionLabel.setText(buildPredictionText(result));
        view.computerMoveLabel.setText("Computer move: " + formatMove(result.getComputerMove()));
        view.resultLabel.setText("Round result: " + gameRules.getRoundMessage(result.getOutcome()));
        view.gameStatusLabel.setText(result.isGameOver()
                ? "Game over. " + gameRules.getOverallResultMessage(result.getHumanWins(), result.getComputerWins())
                : "Game in progress.");
        view.humanWinsLabel.setText("Human wins: " + result.getHumanWins());
        view.computerWinsLabel.setText("Computer wins: " + result.getComputerWins());
        view.tiesLabel.setText("Ties: " + result.getTies());

        if (result.isGameOver()) {
            view.setMoveButtonsDisabled(true);
            showGameOver(result);
        }
    }

    private void updateStrategySelection() {
        intelligentMode = view.intelligentRadio.isSelected();
        String strategyName = StrategyFactory.getStrategyName(intelligentMode);
        view.modeLabel.setText("Mode: " + strategyName);

        if (gameSession == null || gameSession.isGameOver()) {
            view.strategySelectionLabel.setText("Selected strategy: " + strategyName);
        } else {
            view.strategySelectionLabel.setText("Selected strategy for next game: " + strategyName);
        }
    }

    private Integer getRequestedRoundCount() {
        try {
            int rounds = Integer.parseInt(view.roundsSpinner.getEditor().getText().trim());
            if (rounds < 1 || rounds > 100) {
                showError("Round count must be between 1 and 100.");
                return null;
            }

            view.roundsSpinner.getValueFactory().setValue(rounds);
            return rounds;
        } catch (NumberFormatException e) {
            showError("Round count must be a whole number.");
            return null;
        }
    }

    private Player promptForPlayer() {
        while (true) {
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.initOwner(primaryStage);
            usernameDialog.setTitle("Player Login");
            usernameDialog.setHeaderText("Enter a username to load or create a player.");
            usernameDialog.setContentText("Username:");

            Optional<String> usernameResult = usernameDialog.showAndWait();
            if (usernameResult.isEmpty()) {
                return null;
            }

            String username = usernameResult.get().trim();
            if (username.isEmpty()) {
                showError("Username cannot be empty.");
                continue;
            }

            Player existing = userManagement.findByUsername(username);
            if (existing != null) {
                return existing;
            }

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.initOwner(primaryStage);
            nameDialog.setTitle("Create Player");
            nameDialog.setHeaderText("No player found. Create a new one.");
            nameDialog.setContentText("Display name:");

            Optional<String> nameResult = nameDialog.showAndWait();
            if (nameResult.isEmpty()) {
                return null;
            }

            String displayName = nameResult.get().trim();
            if (displayName.isEmpty()) {
                showError("Display name cannot be empty.");
                continue;
            }

            Player player = userManagement.createPlayer(username, displayName);
            dataLoader.store(gson, userManagement.getPlayersAsArray());
            return player;
        }
    }

    private void showWaitingState() {
        view.modeLabel.setText("Mode: Intelligent AI");
        view.playerLabel.setText("Player: waiting for login");
        view.strategyLabel.setText("Current strategy: waiting for game");
        view.roundLabel.setText("Round: 1 of " + DEFAULT_ROUNDS);
        view.predictionLabel.setText("Prediction: waiting for player");
        view.computerMoveLabel.setText("Computer move: waiting for player");
        view.resultLabel.setText("Round result: waiting for player");
        view.gameStatusLabel.setText("Enter a username to begin.");
        view.humanWinsLabel.setText("Human wins: 0");
        view.computerWinsLabel.setText("Computer wins: 0");
        view.tiesLabel.setText("Ties: 0");
        view.strategySelectionLabel.setText("Selected strategy: Intelligent AI");
        roundHistory.clear();
    }

    private void showAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        about.initOwner(primaryStage);
        about.setTitle("About");
        about.setHeaderText("Rock Paper Scissors");
        about.setContentText("CS 151 Assignment 5 JavaFX version.");
        about.showAndWait();
    }

    private void showGameOver(RoundResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.initOwner(primaryStage);
        alert.setTitle("Game Over");
        alert.setHeaderText(gameRules.getOverallResultMessage(result.getHumanWins(), result.getComputerWins()));
        alert.setContentText("Use Start New Game to play again.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.initOwner(primaryStage);
        alert.setHeaderText("Input Error");
        alert.showAndWait();
    }

    private String buildPlayerLabel() {
        if (currentPlayer.getFavoriteMove() == null) {
            return "Player: " + currentPlayer.getName() + " (" + currentPlayer.getUserName() + ")";
        }

        return "Player: " + currentPlayer.getName() + " (" + currentPlayer.getUserName()
                + ")  Favorite move: " + currentPlayer.getFavoriteMove();
    }

    private String buildRoundLabel() {
        if (gameSession == null) {
            return "Round: 1 of " + DEFAULT_ROUNDS;
        }

        if (gameSession.isGameOver()) {
            return "Round: " + gameSession.getTotalRounds() + " of " + gameSession.getTotalRounds();
        }

        return "Round: " + gameSession.getCurrentRound() + " of " + gameSession.getTotalRounds();
    }

    private String buildStartingPredictionText() {
        if (!intelligentMode) {
            return "Prediction: Random AI does not predict moves.";
        }

        return "Prediction: Intelligent AI will predict once it has enough history.";
    }

    private String buildPredictionText(RoundResult result) {
        if ("Random AI".equals(result.getStrategyName())) {
            return "Prediction: Random AI does not predict moves.";
        }

        if (result.getPredictedHumanMove() == null) {
            return "Prediction: not enough matching history yet.";
        }

        String message = "Prediction: " + formatMove(result.getPredictedHumanMove());
        if (result.getPredictionConfidence() != null) {
            message += " (" + formatConfidence(result.getPredictionConfidence()) + ")";
        }
        return message;
    }

    private String buildHistoryEntry(RoundResult result) {
        return "Round " + result.getRoundNumber()
                + ": Human " + formatMove(result.getHumanMove())
                + " | Computer " + formatMove(result.getComputerMove())
                + " | " + switch (result.getOutcome()) {
                    case HUMAN_WIN -> "Human wins";
                    case COMPUTER_WIN -> "Computer wins";
                    case TIE -> "Tie";
                };
    }

    private String formatMove(Move move) {
        String name = move.getName();
        return name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
    }

    private String formatConfidence(double confidence) {
        return String.format(Locale.US, "%.0f%%", confidence * 100.0);
    }
}
