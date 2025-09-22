package com.sudoku.manager;

import com.sudoku.model.SudokuBoard;
import com.sudoku.model.Difficulty;
import com.sudoku.generator.SudokuGenerator;
import com.sudoku.solver.SudokuSolver;
import com.sudoku.persistence.GameState;
import com.sudoku.persistence.GamePersistence;
import java.util.*;

public class GameManager {
    private SudokuBoard currentBoard;
    private SudokuBoard originalBoard;
    private SudokuGenerator generator;
    private SudokuSolver solver;
    private long startTime;
    private boolean gameCompleted;
    private int hintsUsed;
    private List<GameMove> moveHistory;
    private Difficulty currentDifficulty;
    private boolean customInputMode = false;
    
    public GameManager() {
        this.generator = new SudokuGenerator();
        this.solver = new SudokuSolver();
        this.moveHistory = new ArrayList<>();
        this.hintsUsed = 0;
    }
    
    public void startNewGame(Difficulty difficulty) {
        currentBoard = generator.generatePuzzle(difficulty);
        originalBoard = currentBoard.copy();
        currentDifficulty = difficulty;
        startTime = System.currentTimeMillis();
        gameCompleted = false;
        hintsUsed = 0;
        moveHistory.clear();
    }
    
    public boolean makeMove(int row, int col, int value) {
        if (gameCompleted || !currentBoard.isValidPosition(row, col)) {
            return false;
        }
        
        if (!customInputMode && currentBoard.getCell(row, col).isFixed()) {
            return false; // Cannot modify fixed cells (except in custom input mode)
        }
        
        int previousValue = currentBoard.getValue(row, col);
        
        if (customInputMode || value == 0 || currentBoard.isValidMove(row, col, value)) {
            currentBoard.setValue(row, col, value);
            if (!customInputMode) {
                moveHistory.add(new GameMove(row, col, previousValue));
                
                if (currentBoard.isComplete()) {
                    gameCompleted = true;
                }
            }
            return true;
        }
        
        return false;
    }
    
    public boolean undoMove() {
        if (moveHistory.isEmpty() || gameCompleted) {
            return false;
        }
        
        GameMove lastMove = moveHistory.remove(moveHistory.size() - 1);
        currentBoard.setValue(lastMove.row, lastMove.col, lastMove.previousValue);
        
        return true;
    }
    
    public List<String> getHint() {
        if (gameCompleted) {
            return Arrays.asList("Game is already completed!");
        }
        
        List<String> hints = solver.getHint(currentBoard);
        if (!hints.isEmpty()) {
            hintsUsed++;
        }
        
        return hints.isEmpty() ? Arrays.asList("No obvious hints available.") : hints;
    }
    
    public boolean solvePuzzle() {
        if (gameCompleted) {
            return false;
        }
        
        SudokuBoard copy = currentBoard.copy();
        if (solver.solve(copy)) {
            currentBoard = copy;
            gameCompleted = true;
            return true;
        }
        
        return false;
    }
    
    public void resetToOriginal() {
        if (originalBoard != null) {
            currentBoard = originalBoard.copy();
            gameCompleted = false;
            moveHistory.clear();
            hintsUsed = 0;
            startTime = System.currentTimeMillis();
        }
    }
    
    public boolean isGameCompleted() {
        return gameCompleted;
    }
    
    public SudokuBoard getCurrentBoard() {
        return currentBoard;
    }
    
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    public String getFormattedTime() {
        long elapsed = getElapsedTime() / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public int getHintsUsed() {
        return hintsUsed;
    }
    
    public int getMoveCount() {
        return moveHistory.size();
    }
    
    public GameStats getGameStats() {
        return new GameStats(
            getFormattedTime(),
            getMoveCount(),
            getHintsUsed(),
            isGameCompleted()
        );
    }
    
    public boolean validateCurrentState() {
        return solver.isValid(currentBoard);
    }
    
    public boolean saveGame(String filename, String playerName) {
        if (currentBoard == null) return false;
        
        GamePersistence persistence = new GamePersistence();
        GameState gameState = new GameState(
            currentBoard.getBoardArray(),
            originalBoard.getBoardArray(),
            currentBoard.getFixedCellsArray(),
            currentDifficulty,
            startTime,
            gameCompleted,
            hintsUsed,
            playerName
        );
        
        return persistence.saveGame(gameState, filename);
    }
    
    public boolean loadGame(String filename) {
        GamePersistence persistence = new GamePersistence();
        GameState gameState = persistence.loadGame(filename);
        
        if (gameState == null) return false;
        
        currentBoard = new SudokuBoard(gameState.getCurrentBoard(), gameState.getFixedCells());
        originalBoard = new SudokuBoard(gameState.getOriginalBoard(), gameState.getFixedCells());
        currentDifficulty = gameState.getDifficulty();
        startTime = gameState.getStartTime();
        gameCompleted = gameState.isGameCompleted();
        hintsUsed = gameState.getHintsUsed();
        moveHistory.clear();
        
        return true;
    }
    
    public List<String> listSavedGames() {
        GamePersistence persistence = new GamePersistence();
        return persistence.listSavedGames();
    }
    
    public boolean deleteGame(String filename) {
        GamePersistence persistence = new GamePersistence();
        return persistence.deleteGame(filename);
    }
    
    public CustomPuzzleResult startCustomPuzzle(int[][] inputGrid) {
        // Validate input grid
        SudokuBoard testBoard = new SudokuBoard();
        
        // Set input values and mark as fixed
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (inputGrid[row][col] != 0) {
                    if (inputGrid[row][col] < 1 || inputGrid[row][col] > 9) {
                        return new CustomPuzzleResult(false, "Invalid number at row " + (row+1) + ", col " + (col+1) + ": " + inputGrid[row][col]);
                    }
                    testBoard.setValue(row, col, inputGrid[row][col]);
                    testBoard.getCell(row, col).setFixed(true);
                }
            }
        }
        
        // Check for immediate conflicts
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (inputGrid[row][col] != 0 && testBoard.hasConflict(row, col)) {
                    return new CustomPuzzleResult(false, "Conflict detected at row " + (row+1) + ", col " + (col+1));
                }
            }
        }
        
        // Check if puzzle has a unique solution
        SudokuBoard solutionBoard = testBoard.copy();
        int solutionCount = solver.countSolutions(solutionBoard, 2); // Check up to 2 solutions
        
        if (solutionCount == 0) {
            return new CustomPuzzleResult(false, "No solution exists for this puzzle");
        }
        if (solutionCount > 1) {
            return new CustomPuzzleResult(false, "Multiple solutions exist - puzzle is not unique");
        }
        
        // Puzzle is valid, set up the game
        currentBoard = testBoard;
        originalBoard = testBoard.copy();
        currentDifficulty = Difficulty.MEDIUM; // Default for custom puzzles
        startTime = System.currentTimeMillis();
        gameCompleted = false;
        hintsUsed = 0;
        moveHistory.clear();
        
        return new CustomPuzzleResult(true, "Custom puzzle loaded successfully");
    }
    
    public boolean solveCustomPuzzle() {
        if (currentBoard == null) return false;
        
        SudokuBoard copy = currentBoard.copy();
        if (solver.solve(copy)) {
            currentBoard = copy;
            gameCompleted = true;
            return true;
        }
        return false;
    }
    
    public void startCustomInputMode() {
        currentBoard = new SudokuBoard();
        originalBoard = null;
        currentDifficulty = Difficulty.MEDIUM;
        startTime = System.currentTimeMillis();
        gameCompleted = false;
        hintsUsed = 0;
        moveHistory.clear();
        customInputMode = true;
    }
    
    public boolean isCustomInputMode() {
        return customInputMode;
    }
    
    public CustomPuzzleResult finishCustomInput() {
        if (!customInputMode) {
            return new CustomPuzzleResult(false, "Not in custom input mode");
        }
        
        // Get current grid state
        int[][] inputGrid = currentBoard.getBoardArray();
        
        // Mark all non-empty cells as fixed
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (inputGrid[row][col] != 0) {
                    currentBoard.getCell(row, col).setFixed(true);
                }
            }
        }
        
        // Validate the puzzle
        CustomPuzzleResult result = validateCustomPuzzle(inputGrid);
        if (result.isSuccess()) {
            originalBoard = currentBoard.copy();
            customInputMode = false;
        }
        
        return result;
    }
    
    private CustomPuzzleResult validateCustomPuzzle(int[][] inputGrid) {
        // Check for conflicts
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (inputGrid[row][col] != 0 && currentBoard.hasConflict(row, col)) {
                    return new CustomPuzzleResult(false, "Conflict detected at row " + (row+1) + ", col " + (col+1));
                }
            }
        }
        
        // Check if puzzle has a unique solution
        SudokuBoard testBoard = currentBoard.copy();
        int solutionCount = solver.countSolutions(testBoard, 2);
        
        if (solutionCount == 0) {
            return new CustomPuzzleResult(false, "No solution exists for this puzzle");
        }
        if (solutionCount > 1) {
            return new CustomPuzzleResult(false, "Multiple solutions exist - puzzle is not unique");
        }
        
        return new CustomPuzzleResult(true, "Custom puzzle is valid");
    }
    
    public static class CustomPuzzleResult {
        private final boolean success;
        private final String message;
        
        public CustomPuzzleResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    // Inner classes
    private static class GameMove {
        final int row, col, previousValue;
        
        GameMove(int row, int col, int previousValue) {
            this.row = row;
            this.col = col;
            this.previousValue = previousValue;
        }
    }
    
    public static class GameStats {
        private final String timeElapsed;
        private final int moveCount;
        private final int hintsUsed;
        private final boolean completed;
        
        public GameStats(String timeElapsed, int moveCount, int hintsUsed, boolean completed) {
            this.timeElapsed = timeElapsed;
            this.moveCount = moveCount;
            this.hintsUsed = hintsUsed;
            this.completed = completed;
        }
        
        public String getTimeElapsed() { return timeElapsed; }
        public int getMoveCount() { return moveCount; }
        public int getHintsUsed() { return hintsUsed; }
        public boolean isCompleted() { return completed; }
        
        @Override
        public String toString() {
            return String.format("Time: %s | Moves: %d | Hints: %d | Status: %s",
                timeElapsed, moveCount, hintsUsed, completed ? "Completed" : "In Progress");
        }
    }
}