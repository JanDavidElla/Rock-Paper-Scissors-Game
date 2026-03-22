## Rock Paper Scissors Game

A simple classic Rock Paper Scissors game in Java for CS151.

### Setup (Maven)

1. Install [Maven](https://maven.apache.org/download.cgi) if you haven't already, (run `mvn -v` to check if you have it already)
   - macOS: use brew, `brew install maven`
   - Windows: download the zip, extract it, and add the `bin` folder to your PATH
2. From the project root directory, compile with:
   ```
   mvn compile
   ```
3. Run the app:
   ```
   mvn "exec:java" "-Dexec.mainClass=depen.App"
   ```

### How to Run

1. Run the main class to start playing
2. Enter your choice: Rock, Paper, or Scissors
3. The game will compare your choice to the computer's choice (Finds out your most likely sequential move and chooses its opposite) and declare a winner

### Rules

- Rock beats Scissors
- Scissors beats Paper
- Paper beats Rock
