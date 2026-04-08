package depen;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class AppView {
    private static final double STATUS_WIDTH = 520;
    private static final double STATS_WIDTH = 220;
    private static final double CONTENT_WIDTH = STATUS_WIDTH + STATS_WIDTH + 18;

    final Label modeLabel = new Label();
    final Label playerLabel = new Label();
    final Label strategyLabel = new Label();
    final Label strategySelectionLabel = new Label();
    final Label roundLabel = new Label();
    final Label predictionLabel = new Label();
    final Label computerMoveLabel = new Label();
    final Label resultLabel = new Label();
    final Label gameStatusLabel = new Label();
    final Label humanWinsLabel = new Label();
    final Label computerWinsLabel = new Label();
    final Label tiesLabel = new Label();

    final Spinner<Integer> roundsSpinner = new Spinner<>(1, 100, 20);
    final RadioButton intelligentRadio = new RadioButton("Intelligent AI");
    final RadioButton randomRadio = new RadioButton("Random AI");
    final ListView<String> historyListView;

    private final Button rockButton;
    private final Button paperButton;
    private final Button scissorsButton;
    private final BorderPane root;

    public AppView(ObservableList<String> roundHistory,
                   Consumer<Move> onMove,
                   Runnable onNewGame,
                   Runnable onExit,
                   Runnable onAbout,
                   Runnable onStrategyChanged) {
        this.historyListView = new ListView<>(roundHistory);
        this.rockButton = createMoveButton("Rock", Move.ROCK, onMove);
        this.paperButton = createMoveButton("Paper", Move.PAPER, onMove);
        this.scissorsButton = createMoveButton("Scissors", Move.SCISSORS, onMove);
        this.root = createRoot(onNewGame, onExit, onAbout, onStrategyChanged);
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setMoveButtonsDisabled(boolean disabled) {
        rockButton.setDisable(disabled);
        paperButton.setDisable(disabled);
        scissorsButton.setDisable(disabled);
    }

    private BorderPane createRoot(Runnable onNewGame, Runnable onExit, Runnable onAbout, Runnable onStrategyChanged) {
        BorderPane pane = new BorderPane();
        pane.setTop(createMenuBar(onNewGame, onExit, onAbout));
        pane.setCenter(createMainContent(onNewGame, onStrategyChanged));
        pane.setStyle("-fx-background-color: #f3f4f6;");
        return pane;
    }

    private MenuBar createMenuBar(Runnable onNewGame, Runnable onExit, Runnable onAbout) {
        MenuItem newGameItem = new MenuItem("Start New Game");
        newGameItem.setOnAction(event -> onNewGame.run());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> onExit.run());

        Menu gameMenu = new Menu("Game");
        gameMenu.getItems().addAll(newGameItem, exitItem);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(event -> onAbout.run());

        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(gameMenu, helpMenu);
    }

    private VBox createMainContent(Runnable onNewGame, Runnable onStrategyChanged) {
        VBox wrapper = new VBox(16,
                createTopBar(onNewGame),
                createStatusRow(),
                createMoveRow(),
                createControls(onStrategyChanged),
                createHistory());

        wrapper.setPadding(new Insets(18));
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPrefWidth(CONTENT_WIDTH);
        wrapper.setMaxWidth(CONTENT_WIDTH);
        return wrapper;
    }

    private BorderPane createTopBar(Runnable onNewGame) {
        modeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button startButton = new Button("Start New Game");
        startButton.setOnAction(event -> onNewGame.run());

        HBox right = new HBox(startButton);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region leftSpacer = new Region();
        leftSpacer.prefWidthProperty().bind(right.widthProperty());

        BorderPane top = new BorderPane();
        top.setLeft(leftSpacer);
        top.setCenter(modeLabel);
        top.setRight(right);
        return top;
    }

    private HBox createStatusRow() {
        playerLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        strategyLabel.setStyle("-fx-font-size: 13px;");
        roundLabel.setStyle("-fx-font-size: 13px;");
        predictionLabel.setStyle("-fx-font-size: 13px;");
        computerMoveLabel.setStyle("-fx-font-size: 13px;");
        resultLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        gameStatusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        VBox left = new VBox(8,
                playerLabel,
                strategyLabel,
                roundLabel,
                predictionLabel,
                computerMoveLabel,
                resultLabel,
                gameStatusLabel);
        left.setPadding(new Insets(16));
        left.setPrefWidth(STATUS_WIDTH);
        left.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db;");

        Label statsTitle = new Label("Stats");
        statsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        humanWinsLabel.setStyle("-fx-font-size: 13px;");
        computerWinsLabel.setStyle("-fx-font-size: 13px;");
        tiesLabel.setStyle("-fx-font-size: 13px;");

        VBox right = new VBox(8, statsTitle, humanWinsLabel, computerWinsLabel, tiesLabel);
        right.setPadding(new Insets(16));
        right.setPrefWidth(STATS_WIDTH);
        right.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db;");

        HBox row = new HBox(18, left, right);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private HBox createMoveRow() {
        HBox row = new HBox(16, rockButton, paperButton, scissorsButton);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox createControls(Runnable onStrategyChanged) {
        roundsSpinner.setEditable(true);

        ToggleGroup group = new ToggleGroup();
        intelligentRadio.setToggleGroup(group);
        randomRadio.setToggleGroup(group);
        intelligentRadio.setSelected(true);

        intelligentRadio.setOnAction(event -> onStrategyChanged.run());
        randomRadio.setOnAction(event -> onStrategyChanged.run());

        HBox roundsRow = new HBox(10, new Label("Rounds for next game:"), roundsSpinner);
        roundsRow.setAlignment(Pos.CENTER_LEFT);

        HBox strategyRow = new HBox(10, new Label("Strategy for next game:"), intelligentRadio, randomRadio);
        strategyRow.setAlignment(Pos.CENTER_LEFT);

        strategySelectionLabel.setStyle("-fx-font-size: 12px;");

        VBox box = new VBox(8, roundsRow, strategyRow, strategySelectionLabel);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db;");
        return box;
    }

    private VBox createHistory() {
        Label title = new Label("Round History");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        historyListView.setPlaceholder(new Label("No rounds yet."));
        historyListView.setPrefHeight(210);

        VBox box = new VBox(8, title, historyListView);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db;");
        return box;
    }

    private Button createMoveButton(String text, Move move, Consumer<Move> onMove) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setOnAction(event -> onMove.accept(move));
        return button;
    }
}
