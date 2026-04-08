package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class App extends Application {

    private static final int DEFAULT_ROUNDS = 20;
    private static final double STATUS_CARD_WIDTH = 520;
    private static final double STATS_CARD_WIDTH = 220;
    private static final double CONTENT_WIDTH = STATUS_CARD_WIDTH + STATS_CARD_WIDTH + 18;
    private static final String TOP_BAR_BUTTON_STYLE = "-fx-font-size: 13px;"
            + "-fx-font-weight: bold;"
            + "-fx-background-color: rgba(255,255,255,0.9);"
            + "-fx-text-fill: #2d4a63;"
            + "-fx-border-color: #415a77;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;"
            + "-fx-padding: 8 14 8 14;";
    private static final String TOP_BAR_BUTTON_HOVER_STYLE = "-fx-font-size: 13px;"
            + "-fx-font-weight: bold;"
            + "-fx-background-color: rgba(232,239,247,0.98);"
            + "-fx-text-fill: #20384f;"
            + "-fx-border-color: #415a77;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;"
            + "-fx-padding: 8 14 8 14;";
    private static final String MOVE_BUTTON_STYLE = "-fx-font-size: 15px;"
            + "-fx-font-weight: bold;"
            + "-fx-background-color: #415a77;"
            + "-fx-text-fill: white;"
            + "-fx-background-radius: 12;";
    private static final String MOVE_BUTTON_HOVER_STYLE = "-fx-font-size: 15px;"
            + "-fx-font-weight: bold;"
            + "-fx-background-color: #334b63;"
            + "-fx-text-fill: white;"
            + "-fx-background-radius: 12;";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final DataLoader dataLoader = new DataLoader(".", "data.json");
    private final GameRules gameRules = new GameRules();
    private final ChoiceStrategy predictionStrategy = new Prediction();
    private final ChoiceStrategy randomStrategy = new RandomStrategy();
    private ChoiceStrategy selectedStrategy = predictionStrategy;

    private UserManagement userManagement;
    private Player currentPlayer;
    private GameSession gameSession;
    private Stage primaryStage;

    private final ObservableList<String> roundHistory = FXCollections.observableArrayList();

    private Label modeLabel;
    private Label playerLabel;
    private Label strategyLabel;
    private Label strategySelectionLabel;
    private Label roundLabel;
    private Label predictionLabel;
    private Label computerMoveLabel;
    private Label resultLabel;
    private Label gameStatusLabel;
    private Label humanWinsLabel;
    private Label computerWinsLabel;
    private Label tiesLabel;
    private Spinner<Integer> roundsSpinner;
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private RadioButton intelligentRadio;
    private RadioButton randomRadio;
    private ListView<String> historyListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(dataLoader.load(gson)));
        userManagement = new UserManagement(players);
        primaryStage = stage;

        stage.setTitle("CS 151 Assignment 5 - Rock Paper Scissors");
        stage.setScene(new Scene(createRoot(), 850, 900));
        stage.setMinWidth(800);
        stage.setMinHeight(900);
        setMoveButtonsDisabled(true);
        showWaitingState();
        stage.show();

        Platform.runLater(() -> {
            stage.toFront();
            stage.requestFocus();
        });
        Platform.runLater(this::finishStartup);
    }

    @Override
    public void stop() {
        if (gameSession != null) {
            gameSession.closeSession();
        } else if (currentPlayer != null) {
            currentPlayer.setFavoriteMove();
            userManagement.savePlayer(currentPlayer);
            dataLoader.store(gson, userManagement.getPlayersAsArray());
        }

        predictionStrategy.onGameEnd();
    }

    private BorderPane createRoot() {
        VBox mainContentWrapper = createMainContentWrapper();

        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(mainContentWrapper);
        BorderPane.setAlignment(mainContentWrapper, Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f4ea, #dfe8f6);");
        return root;
    }

    private MenuBar createMenuBar() {
        MenuItem startNewGameItem = new MenuItem("Start New Game");
        startNewGameItem.setOnAction(event -> handleStartNewGameRequest());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());

        Menu gameMenu = new Menu("Game");
        gameMenu.getItems().addAll(startNewGameItem, exitItem);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(event -> showAboutDialog());

        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(gameMenu, helpMenu);
    }

    private VBox createMainContentWrapper() {
        BorderPane topBar = createTopBar();
        HBox infoRow = createInfoRow();
        HBox buttonRow = createMoveButtonsRow();
        VBox controlsCard = createControlsCard();
        VBox historyCard = createHistoryCard();

        VBox mainContentWrapper = new VBox(20, topBar, infoRow, buttonRow, controlsCard, historyCard);
        mainContentWrapper.setPadding(new Insets(20));
        mainContentWrapper.setAlignment(Pos.TOP_CENTER);
        mainContentWrapper.setFillWidth(true);
        mainContentWrapper.setPrefWidth(CONTENT_WIDTH);
        mainContentWrapper.setMaxWidth(CONTENT_WIDTH);
        return mainContentWrapper;
    }

    private BorderPane createTopBar() {
        modeLabel = new Label();
        modeLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d4a63;");

        Button startNewGameButton = createTopBarButton("Start New Game");
        startNewGameButton.setOnAction(event -> handleStartNewGameRequest());

        HBox buttonBox = new HBox(10, startNewGameButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Region leftSpacer = new Region();
        leftSpacer.prefWidthProperty().bind(buttonBox.widthProperty());
        leftSpacer.setMinWidth(Region.USE_PREF_SIZE);

        HBox modeBox = new HBox(modeLabel);
        modeBox.setAlignment(Pos.CENTER);

        BorderPane topBar = new BorderPane();
        topBar.setLeft(leftSpacer);
        topBar.setCenter(modeBox);
        topBar.setRight(buttonBox);
        topBar.setPadding(new Insets(15, 0, 15, 0));
        topBar.setPrefWidth(CONTENT_WIDTH);
        topBar.setMaxWidth(Double.MAX_VALUE);
        return topBar;
    }

    private HBox createInfoRow() {
        playerLabel = new Label();
        playerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        strategyLabel = createCardLabel();
        roundLabel = createCardLabel();
        predictionLabel = createCardLabel();
        computerMoveLabel = createCardLabel();
        resultLabel = createCardLabel();
        gameStatusLabel = createCardLabel();

        VBox statusCard = new VBox(
                12,
                playerLabel,
                strategyLabel,
                roundLabel,
                predictionLabel,
                computerMoveLabel,
                resultLabel,
                gameStatusLabel);
        statusCard.setPadding(new Insets(24));
        statusCard.setPrefWidth(STATUS_CARD_WIDTH);
        statusCard.setStyle("-fx-background-color: rgba(255,255,255,0.88);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #415a77;"
                + "-fx-border-radius: 18;");

        humanWinsLabel = createStatLabel();
        computerWinsLabel = createStatLabel();
        tiesLabel = createStatLabel();

        Label statsTitle = new Label("Running Statistics");
        statsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox statsCard = new VBox(12, statsTitle, humanWinsLabel, computerWinsLabel, tiesLabel);
        statsCard.setPadding(new Insets(24));
        statsCard.setPrefWidth(STATS_CARD_WIDTH);
        statsCard.setStyle("-fx-background-color: rgba(255,255,255,0.88);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #c08b5c;"
                + "-fx-border-radius: 18;");

        HBox infoRow = new HBox(18, statusCard, statsCard);
        infoRow.setAlignment(Pos.CENTER);
        infoRow.setPrefWidth(CONTENT_WIDTH);
        infoRow.setMaxWidth(Double.MAX_VALUE);
        return infoRow;
    }

    private HBox createMoveButtonsRow() {
        rockButton = createMoveButton("Rock", Move.ROCK);
        paperButton = createMoveButton("Paper", Move.PAPER);
        scissorsButton = createMoveButton("Scissors", Move.SCISSORS);

        HBox buttonRow = new HBox(20, rockButton, paperButton, scissorsButton);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPrefWidth(CONTENT_WIDTH);
        buttonRow.setMaxWidth(Double.MAX_VALUE);
        return buttonRow;
    }

    private VBox createControlsCard() {
        roundsSpinner = new Spinner<>(1, 100, DEFAULT_ROUNDS);
        roundsSpinner.setEditable(true);

        intelligentRadio = new RadioButton("Intelligent AI");
        randomRadio = new RadioButton("Random AI");
        ToggleGroup strategyGroup = new ToggleGroup();
        intelligentRadio.setToggleGroup(strategyGroup);
        randomRadio.setToggleGroup(strategyGroup);
        intelligentRadio.setSelected(true);

        intelligentRadio.setOnAction(event -> updateSelectedStrategy());
        randomRadio.setOnAction(event -> updateSelectedStrategy());

        Label roundsLabel = new Label("Rounds for next game:");
        roundsLabel.setStyle("-fx-font-weight: bold;");
        Label strategyChooserLabel = new Label("Strategy for next game:");
        strategyChooserLabel.setStyle("-fx-font-weight: bold;");

        strategySelectionLabel = new Label();
        strategySelectionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #44556b;");

        HBox roundsBox = new HBox(10, roundsLabel, roundsSpinner);
        roundsBox.setAlignment(Pos.CENTER_LEFT);

        HBox strategyBox = new HBox(10, strategyChooserLabel, intelligentRadio, randomRadio);
        strategyBox.setAlignment(Pos.CENTER_LEFT);

        VBox controlsCard = new VBox(10, roundsBox, strategyBox, strategySelectionLabel);
        controlsCard.setPadding(new Insets(20));
        controlsCard.setPrefWidth(CONTENT_WIDTH);
        controlsCard.setMaxWidth(Double.MAX_VALUE);
        controlsCard.setStyle("-fx-background-color: rgba(255,255,255,0.88);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #7b9e87;"
                + "-fx-border-radius: 18;");
        return controlsCard;
    }

    private VBox createHistoryCard() {
        Label historyTitle = new Label("Round History");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        historyListView = new ListView<>(roundHistory);
        historyListView.setPlaceholder(new Label("No rounds played yet."));
        historyListView.setPrefHeight(220);
        historyListView.setMaxWidth(Double.MAX_VALUE);

        VBox historyCard = new VBox(12, historyTitle, historyListView);
        historyCard.setPadding(new Insets(20));
        historyCard.setPrefWidth(CONTENT_WIDTH);
        historyCard.setMaxWidth(Double.MAX_VALUE);
        historyCard.setStyle("-fx-background-color: rgba(255,255,255,0.88);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #8b6f47;"
                + "-fx-border-radius: 18;");

        updateSelectedStrategy();
        return historyCard;
    }

    private Button createTopBarButton(String title) {
        Button button = new Button(title);
        button.setStyle(TOP_BAR_BUTTON_STYLE);
        button.setCursor(Cursor.HAND);
        button.setOnMouseEntered(event -> button.setStyle(TOP_BAR_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(event -> button.setStyle(TOP_BAR_BUTTON_STYLE));
        return button;
    }

    private Label createCardLabel() {
        Label label = new Label();
        label.setWrapText(true);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setStyle("-fx-font-size: 14px;");
        return label;
    }

    private Label createStatLabel() {
        Label label = new Label();
        label.setStyle("-fx-font-size: 14px;");
        return label;
    }

    private Button createMoveButton(String title, Move move) {
        Button button = new Button(title);
        button.setPrefWidth(150);
        button.setStyle(MOVE_BUTTON_STYLE);
        button.setCursor(Cursor.HAND);
        button.setOnMouseEntered(event -> button.setStyle(MOVE_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(event -> button.setStyle(MOVE_BUTTON_STYLE));
        button.setOnAction(event -> playRound(move));
        return button;
    }

    private void handleStartNewGameRequest() {
        Integer rounds = getRequestedRoundCount();
        if (rounds != null && currentPlayer != null) {
            startNewGame(rounds);
        }
    }

    private void playRound(Move move) {
        RoundResult result = gameSession.playRound(move);
        roundHistory.add(buildHistoryEntry(result));
        historyListView.scrollTo(roundHistory.size() - 1);
        updateFromSession(result);
    }

    private void updateFromSession(RoundResult result) {
        playerLabel.setText(buildPlayerLabel());
        strategyLabel.setText("Current strategy: " + result.getStrategyName());
        roundLabel.setText(buildRoundLabel());
        predictionLabel.setText(buildPredictionMessage(result));
        computerMoveLabel.setText("Computer move: " + formatMove(result.getComputerMove()));
        resultLabel.setText("Round result: " + gameRules.getRoundMessage(result.getOutcome()));
        gameStatusLabel.setText(result.isGameOver()
                ? "Game over. " + gameRules.getOverallResultMessage(result.getHumanWins(), result.getComputerWins())
                : "Game in progress.");

        humanWinsLabel.setText("Human wins: " + result.getHumanWins());
        computerWinsLabel.setText("Computer wins: " + result.getComputerWins());
        tiesLabel.setText("Ties: " + result.getTies());

        applyRoundStyles(result);

        if (result.isGameOver()) {
            setMoveButtonsDisabled(true);
            showGameOverDialog(result);
        }
    }

    private void startNewGame(int totalRounds) {
        if (gameSession != null) {
            gameSession.closeSession();
        }

        gameSession = new GameSession(currentPlayer, totalRounds, selectedStrategy, gameRules, userManagement, dataLoader, gson);
        roundHistory.clear();
        roundHistory.add("New game started: " + totalRounds + " rounds using " + gameSession.getStrategyName() + ".");
        setMoveButtonsDisabled(false);
        playerLabel.setText(buildPlayerLabel());
        strategyLabel.setText("Current strategy: " + gameSession.getStrategyName());
        roundLabel.setText(buildRoundLabel());
        predictionLabel.setText(buildStartingPredictionMessage());
        computerMoveLabel.setText("Computer move: Waiting for round");
        resultLabel.setText("Round result: Waiting for player move");
        gameStatusLabel.setText("Game in progress.");
        humanWinsLabel.setText("Human wins: 0");
        computerWinsLabel.setText("Computer wins: 0");
        tiesLabel.setText("Ties: 0");
        applyNeutralStyles();
        updateSelectedStrategy();
    }

    private void finishStartup() {
        currentPlayer = promptForPlayer();

        if (currentPlayer == null) {
            Platform.exit();
            return;
        }

        startNewGame(DEFAULT_ROUNDS);
    }

    private void updateSelectedStrategy() {
        selectedStrategy = intelligentRadio.isSelected() ? predictionStrategy : randomStrategy;
        modeLabel.setText("Mode: " + selectedStrategy.getStrategyName());

        if (gameSession == null || gameSession.isGameOver()) {
            strategySelectionLabel.setText("Selected strategy: " + selectedStrategy.getStrategyName());
        } else {
            strategySelectionLabel.setText("Selected strategy for next game: " + selectedStrategy.getStrategyName());
        }
    }

    private void setMoveButtonsDisabled(boolean disabled) {
        rockButton.setDisable(disabled);
        paperButton.setDisable(disabled);
        scissorsButton.setDisable(disabled);
    }

    private void showWaitingState() {
        playerLabel.setText("Player: waiting for login");
        strategyLabel.setText("Current strategy: waiting for game");
        roundLabel.setText("Round: 1 of " + DEFAULT_ROUNDS);
        predictionLabel.setText("Prediction: Waiting for player");
        computerMoveLabel.setText("Computer move: Waiting for player");
        resultLabel.setText("Round result: Waiting for player");
        gameStatusLabel.setText("Enter a username to begin.");
        humanWinsLabel.setText("Human wins: 0");
        computerWinsLabel.setText("Computer wins: 0");
        tiesLabel.setText("Ties: 0");
        roundHistory.clear();
        applyNeutralStyles();
    }

    private void applyNeutralStyles() {
        resultLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                + "-fx-background-color: #eceff4; -fx-background-radius: 12;");
        gameStatusLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                + "-fx-background-color: #edf2f7; -fx-background-radius: 12;");
    }

    private void applyRoundStyles(RoundResult result) {
        String resultStyle;

        if (result.getOutcome() == RoundOutcome.HUMAN_WIN) {
            resultStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                    + "-fx-background-color: #d7f0dc; -fx-text-fill: #1f5130; -fx-background-radius: 12;";
        } else if (result.getOutcome() == RoundOutcome.COMPUTER_WIN) {
            resultStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                    + "-fx-background-color: #f6d9d3; -fx-text-fill: #7a2f23; -fx-background-radius: 12;";
        } else {
            resultStyle = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                    + "-fx-background-color: #efe7cc; -fx-text-fill: #6f5a1a; -fx-background-radius: 12;";
        }

        resultLabel.setStyle(resultStyle);

        if (result.isGameOver()) {
            gameStatusLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                    + "-fx-background-color: #dbe7f5; -fx-text-fill: #1f3552; -fx-background-radius: 12;"
                    + "-fx-border-color: #415a77; -fx-border-radius: 12;");
        } else {
            gameStatusLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 12 10 12;"
                    + "-fx-background-color: #edf2f7; -fx-text-fill: #2d3748; -fx-background-radius: 12;");
        }
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

    private String buildStartingPredictionMessage() {
        if (selectedStrategy == randomStrategy) {
            return "Prediction: Random AI does not predict your next move.";
        }

        return "Prediction: Intelligent AI will estimate your next move once it has enough matching history.";
    }

    private String buildPredictionMessage(RoundResult result) {
        if ("Random AI".equals(result.getStrategyName())) {
            return "Prediction: Random AI does not predict your next move.";
        }

        if (result.getPredictedHumanMove() == null) {
            return "Prediction: Intelligent AI needs more matching history before it can predict your next move.";
        }

        String message = "Prediction: Intelligent AI predicted you would play "
                + formatMove(result.getPredictedHumanMove());

        if (result.getPredictionConfidence() != null) {
            message += " (" + formatConfidence(result.getPredictionConfidence()) + " confidence)";
        }

        return message + ".";
    }

    private String buildHistoryEntry(RoundResult result) {
        return "Round " + result.getRoundNumber()
                + ": Human " + formatMove(result.getHumanMove())
                + " | Computer " + formatMove(result.getComputerMove())
                + " | " + historyResultText(result.getOutcome());
    }

    private String historyResultText(RoundOutcome outcome) {
        return switch (outcome) {
            case HUMAN_WIN -> "Human wins";
            case COMPUTER_WIN -> "Computer wins";
            case TIE -> "Tie";
        };
    }

    private String formatMove(Move move) {
        if (move == null) {
            return "Unknown";
        }

        String name = move.getName();
        return name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
    }

    private String formatConfidence(double confidence) {
        return String.format(Locale.US, "%.0f%%", confidence * 100.0);
    }

    private Integer getRequestedRoundCount() {
        try {
            int rounds = Integer.parseInt(roundsSpinner.getEditor().getText().trim());
            if (rounds < 1 || rounds > 100) {
                showError("Round count must be between 1 and 100.");
                return null;
            }

            roundsSpinner.getValueFactory().setValue(rounds);
            return rounds;
        } catch (NumberFormatException ex) {
            showError("Round count must be a whole number.");
            return null;
        }
    }

    private Player promptForPlayer() {
        while (true) {
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.initOwner(primaryStage);
            usernameDialog.setTitle("Player Login");
            usernameDialog.setHeaderText("Enter your username to load or create a player.");
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

            Player existingPlayer = userManagement.findByUsername(username);
            if (existingPlayer != null) {
                return existingPlayer;
            }

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.initOwner(primaryStage);
            nameDialog.setTitle("Create Player");
            nameDialog.setHeaderText("No saved player was found. Create a new player.");
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

            Player newPlayer = userManagement.createPlayer(username, displayName);
            dataLoader.store(gson, userManagement.getPlayersAsArray());
            return newPlayer;
        }
    }

    private void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        about.initOwner(primaryStage);
        about.setTitle("About");
        about.setHeaderText("Rock, Paper, Scissors");
        about.setContentText("CS 151 Assignment 5 JavaFX version.\n"
                + "The app reuses the Assignment 4 move, rules, strategy, player, "
                + "data loading, and prediction logic inside a GUI.");
        about.showAndWait();
    }

    private void showGameOverDialog(RoundResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.initOwner(primaryStage);
        alert.setTitle("Game Over");
        alert.setHeaderText(gameRules.getOverallResultMessage(result.getHumanWins(), result.getComputerWins()));
        alert.setContentText("The move buttons are disabled because the game is finished.\n"
                + "Use Start New Game to play again.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.initOwner(primaryStage);
        alert.setHeaderText("Input Error");
        alert.showAndWait();
    }
}
