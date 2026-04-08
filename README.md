# Rock Paper Scissors (CS 151 Assignment 5)

This repository contains a Java Rock Paper Scissors game built for CS 151.
The project started as a console app and now includes a JavaFX GUI.

## Requirements

- Java 21
- Maven 3.9+

Check your versions:

```bash
java -version
mvn -v
```

## Build and Run

From the project root:

```bash
mvn clean compile
mvn javafx:run
```

## What the App Does

- Plays Rock Paper Scissors for a configurable number of rounds
- Supports two computer strategies:
  - Random strategy
  - Prediction-based strategy
- Tracks per-round results and running stats (human wins, computer wins, ties)
- Supports starting a new game from the GUI

## Project Structure

- `src/main/java/depen/App.java` - JavaFX entry point and UI/controller flow
- `src/main/java/depen/GameSession.java` - game session state and round progression
- `src/main/java/depen/GameRules.java` - round outcome and score logic
- `src/main/java/depen/StrategyFactory.java` - strategy selection
- `src/main/java/depen/RandomStrategy.java` - random move selection
- `src/main/java/depen/Prediction.java` - prediction strategy wrapper
- `src/main/java/depen/PredictionModel.java` - prediction algorithm state
- `src/main/java/depen/DataLoader.java` - player data persistence
- `src/main/java/depen/UserManagement.java` - player lookup and creation

## Notes

- Player data is stored in JSON files in the project directory.
- If you are running from an IDE, make sure Maven dependencies are imported and JavaFX is enabled.
