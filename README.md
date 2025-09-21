# Sudoku Game

A complete Sudoku game implementation in Java with console-based interface and extensible architecture for GUI adaptation.

## Features

- **Multiple Difficulty Levels**: Easy, Medium, Hard, and Expert
- **Intelligent Puzzle Generation**: Guarantees unique solutions
- **Advanced Solver**: Backtracking algorithm with MRV heuristic
- **Game Management**: Move history, undo functionality, hints
- **Statistics Tracking**: Time, moves, hints used
- **Input Validation**: Prevents invalid moves and conflicts

## Architecture

### Core Classes

- **`Cell`**: Represents individual Sudoku cells with value and state management
- **`SudokuBoard`**: Manages the 9x9 grid and validation logic
- **`Difficulty`**: Enum defining puzzle difficulty levels
- **`SudokuSolver`**: Implements backtracking algorithm with optimizations
- **`SudokuGenerator`**: Creates valid puzzles with unique solutions
- **`GameManager`**: Handles game state, moves, and statistics
- **`ConsoleUI`**: Provides interactive console interface

### Design Patterns Used

- **Strategy Pattern**: Different difficulty levels
- **Observer Pattern**: Ready for GUI event handling
- **Command Pattern**: Move history for undo functionality
- **Factory Pattern**: Puzzle generation

## Quick Start

### Compilation
```bash
./compile.sh
```

### Running the Game
```bash
java -cp out com.sudoku.SudokuApplication
```

### Manual Compilation
```bash
mkdir -p out
javac -d out -sourcepath src/main/java src/main/java/com/sudoku/*.java src/main/java/com/sudoku/*/*.java
java -cp out com.sudoku.SudokuApplication
```

## How to Play

1. **Start New Game**: Choose difficulty level
2. **Make Moves**: Enter row, column, and value (1-9)
3. **Clear Cells**: Remove values from cells
4. **Get Hints**: Receive suggestions for next moves
5. **Undo Moves**: Revert previous actions
6. **Validate**: Check for conflicts in current state

### Console Commands

- `1 <row> <col> <value>` - Place value in cell
- `2 <row> <col>` - Clear cell
- `3` - Get hint
- `4` - Undo last move
- `5` - Solve puzzle automatically
- `6` - Reset to original puzzle
- `7` - Validate current state
- `8` - Return to main menu

## Extending to GUI

The architecture is designed for easy GUI adaptation:

### JavaFX Integration
```java
public class SudokuFXController {
    private GameManager gameManager = new GameManager();
    
    @FXML
    private GridPane sudokuGrid;
    
    public void initializeBoard() {
        // Create 9x9 grid of TextField nodes
        // Bind to gameManager.getCurrentBoard()
    }
    
    public void handleCellClick(int row, int col) {
        // Handle user input
        // Call gameManager.makeMove()
        // Update UI
    }
}
```

### Swing Integration
```java
public class SudokuSwingPanel extends JPanel {
    private GameManager gameManager = new GameManager();
    private JTextField[][] cells = new JTextField[9][9];
    
    public SudokuSwingPanel() {
        setLayout(new GridLayout(9, 9));
        initializeCells();
    }
    
    private void initializeCells() {
        // Create grid of JTextField components
        // Add DocumentListeners for input validation
    }
}
```

## Algorithm Details

### Puzzle Generation
1. Fill diagonal 3x3 boxes (independent)
2. Use backtracking to complete the board
3. Remove cells while maintaining unique solution
4. Adjust removal count based on difficulty

### Solver Algorithm
- **Backtracking** with constraint propagation
- **MRV Heuristic**: Choose cell with minimum remaining values
- **Forward Checking**: Eliminate impossible values
- **Optimized** for performance with early termination

### Validation
- Row, column, and 3x3 box constraint checking
- Conflict detection for user moves
- Solution uniqueness verification

## Performance Considerations

- **Efficient Data Structures**: Arrays for O(1) access
- **Optimized Algorithms**: MRV heuristic reduces search space
- **Memory Management**: Object reuse where possible
- **Lazy Evaluation**: Hints computed on demand

## Testing Strategy

Create unit tests for:
- `SudokuBoard` validation methods
- `SudokuSolver` correctness and performance
- `SudokuGenerator` puzzle quality
- `GameManager` state management

## Future Enhancements

- **GUI Implementation**: JavaFX or Swing interface
- **Persistence**: Save/load games
- **Multiplayer**: Network-based competition
- **Advanced Hints**: Technique-specific suggestions
- **Themes**: Visual customization
- **Statistics**: Long-term player analytics

## License

This project is open source and available under the MIT License.