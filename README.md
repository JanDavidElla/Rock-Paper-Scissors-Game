# ✊✋✌️ Rock Paper Scissors — Assignment 5


## Overview

This project is an enhanced version of the classic Rock Paper Scissors game built in **Java with JavaFX**. Building on Assignment 4, this iteration introduces a full graphical interface and a machine-learning-inspired strategy that adapts to user behavior over time.

---

## Features

- 🖥️ **JavaFX GUI** — Clean, interactive graphical interface with hover effects and responsive styling
- 🎯 **Strategy Pattern** — Decoupled game logic via the Strategy design pattern
- 🧠 **Two Game Modes:**
  - **Random Strategy** — Computer picks moves at random
  - **Intelligent Strategy** — Predicts your next move based on historical patterns
- 📊 **Live Statistics** — Real-time win/loss/tie tracking
- 🕓 **Round History** — Full log of past rounds in the session
- 🔄 **New Game** — Reset progress at any time
- 📋 **Menu Bar** — `Game → New Game / Exit` and a `Help` section

---

## Technologies Used

| Technology | Version |
|------------|---------|
| Java       | 21      |
| JavaFX     | Latest  |
| Maven      | 3.x     |
| Gson       | Latest  |

---

## Setup

### Prerequisites

Make sure you have **Java 21** installed. Verify with:

```bash
java -version
```

### 1. Install Maven

Check if Maven is already installed:

```bash
mvn -v
```

If not installed:

**macOS (Homebrew):**
```bash
brew install maven
```

**Windows:**
1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract the archive
3. Add the `bin` folder to your system `PATH`

### 2. Clone the Repository

```bash
git clone <your-repo-url>
cd rock-paper-scissors
```

### 3. Compile the Project

```bash
mvn clean compile
```

---

## Running the Application

```bash
mvn javafx:run
```

---

## How to Play

1. Launch the application with `mvn javafx:run`
2. Click **Rock**, **Paper**, or **Scissors**
3. The computer makes its move
4. Results and statistics update in real time
5. Use **Start New Game** to reset all progress

---

## Game Rules

| Move     | Beats     |
|----------|-----------|
| ✊ Rock    | ✌️ Scissors |
| ✌️ Scissors | ✋ Paper   |
| ✋ Paper   | ✊ Rock    |

---

## How It Works

The application uses the **Strategy Pattern** to separate game logic from decision-making, making it easy to swap or extend strategies.

### Random Strategy
The computer selects moves uniformly at random — no prediction, no pattern.

### Intelligent Strategy
The computer builds a frequency map of your move history and predicts your most likely next move, then plays the counter. The longer you play, the smarter it gets.

```
User history → Frequency analysis → Predicted move → Counter move played
```

---

## Improvements from Assignment 4

- ✅ Full JavaFX graphical user interface
- ✅ Intelligent move prediction system
- ✅ Game session and round history tracking
- ✅ Menu bar with New Game and Exit options
- ✅ Button hover effects and improved UI styling
- ✅ Improved code structure and modularity via Strategy Pattern
