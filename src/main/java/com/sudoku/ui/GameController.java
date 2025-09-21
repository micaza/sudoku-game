package com.sudoku.ui;

import com.sudoku.manager.GameManager;
import com.sudoku.model.Difficulty;
import com.sudoku.model.SudokuBoard;

/**
 * Abstract controller class that can be extended for different UI implementations.
 * This provides a clean separation between game logic and UI presentation.
 */
public abstract class GameController {
    protected GameManager gameManager;
    
    public GameController() {
        this.gameManager = new GameManager();
    }
    
    // Abstract methods that UI implementations must provide
    public abstract void updateBoard();
    public abstract void showMessage(String message);
    public abstract void showGameStats();
    public abstract void showHints(java.util.List<String> hints);
    
    // Common controller methods
    public void startNewGame(Difficulty difficulty) {
        gameManager.startNewGame(difficulty);
        updateBoard();
        showMessage("New " + difficulty.name().toLowerCase() + " game started!");
    }
    
    public boolean makeMove(int row, int col, int value) {
        boolean success = gameManager.makeMove(row, col, value);
        updateBoard();
        
        if (success && gameManager.isGameCompleted()) {
            showMessage("Congratulations! Puzzle completed!");
            showGameStats();
        } else if (!success) {
            showMessage("Invalid move. Please try again.");
        }
        
        return success;
    }
    
    public void getHint() {
        java.util.List<String> hints = gameManager.getHint();
        showHints(hints);
    }
    
    public void undoMove() {
        if (gameManager.undoMove()) {
            updateBoard();
            showMessage("Move undone.");
        } else {
            showMessage("No moves to undo.");
        }
    }
    
    public void solvePuzzle() {
        if (gameManager.solvePuzzle()) {
            updateBoard();
            showMessage("Puzzle solved!");
        } else {
            showMessage("Unable to solve puzzle.");
        }
    }
    
    public void resetGame() {
        gameManager.resetToOriginal();
        updateBoard();
        showMessage("Game reset to original state.");
    }
    
    public void validateBoard() {
        if (gameManager.validateCurrentState()) {
            showMessage("Board state is valid - no conflicts.");
        } else {
            showMessage("Board has conflicts - please check your entries.");
        }
    }
    
    // Getters for UI to access game state
    public SudokuBoard getCurrentBoard() {
        return gameManager.getCurrentBoard();
    }
    
    public boolean isGameCompleted() {
        return gameManager.isGameCompleted();
    }
    
    public String getFormattedTime() {
        return gameManager.getFormattedTime();
    }
    
    public int getMoveCount() {
        return gameManager.getMoveCount();
    }
    
    public int getHintsUsed() {
        return gameManager.getHintsUsed();
    }
}