package com.sudoku.manager;

import com.sudoku.model.SudokuBoard;
import com.sudoku.model.Difficulty;
import com.sudoku.generator.SudokuGenerator;
import com.sudoku.solver.SudokuSolver;
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
    
    public GameManager() {
        this.generator = new SudokuGenerator();
        this.solver = new SudokuSolver();
        this.moveHistory = new ArrayList<>();
        this.hintsUsed = 0;
    }
    
    public void startNewGame(Difficulty difficulty) {
        currentBoard = generator.generatePuzzle(difficulty);
        originalBoard = currentBoard.copy();
        startTime = System.currentTimeMillis();
        gameCompleted = false;
        hintsUsed = 0;
        moveHistory.clear();
    }
    
    public boolean makeMove(int row, int col, int value) {
        if (gameCompleted || !currentBoard.isValidPosition(row, col)) {
            return false;
        }
        
        if (currentBoard.getCell(row, col).isFixed()) {
            return false; // Cannot modify fixed cells
        }
        
        int previousValue = currentBoard.getValue(row, col);
        
        if (value == 0 || currentBoard.isValidMove(row, col, value)) {
            currentBoard.setValue(row, col, value);
            moveHistory.add(new GameMove(row, col, previousValue));
            
            if (currentBoard.isComplete()) {
                gameCompleted = true;
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