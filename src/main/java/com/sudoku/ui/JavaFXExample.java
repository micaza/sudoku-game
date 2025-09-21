package com.sudoku.ui;

import com.sudoku.model.Difficulty;
import com.sudoku.model.SudokuBoard;
import java.util.List;

/**
 * Example JavaFX controller implementation.
 * This shows how to extend GameController for JavaFX integration.
 * 
 * To use this with JavaFX:
 * 1. Add JavaFX dependencies to your project
 * 2. Create FXML file with GridPane for the board
 * 3. Implement the abstract methods with actual JavaFX components
 */
public class JavaFXExample extends GameController {
    
    // JavaFX components would be injected here with @FXML
    // @FXML private GridPane boardGrid;
    // @FXML private Label timeLabel;
    // @FXML private Label movesLabel;
    // @FXML private Label hintsLabel;
    // @FXML private TextArea messageArea;
    
    @Override
    public void updateBoard() {
        SudokuBoard board = getCurrentBoard();
        if (board == null) return;
        
        // Example implementation:
        /*
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = getCellTextField(row, col);
                int value = board.getValue(row, col);
                
                if (value == 0) {
                    cell.setText("");
                } else {
                    cell.setText(String.valueOf(value));
                }
                
                // Style fixed cells differently
                if (board.getCell(row, col).isFixed()) {
                    cell.getStyleClass().add("fixed-cell");
                } else {
                    cell.getStyleClass().remove("fixed-cell");
                }
            }
        }
        
        // Update status labels
        timeLabel.setText("Time: " + getFormattedTime());
        movesLabel.setText("Moves: " + getMoveCount());
        hintsLabel.setText("Hints: " + getHintsUsed());
        */
        
        System.out.println("Board updated (JavaFX implementation needed)");
    }
    
    @Override
    public void showMessage(String message) {
        // messageArea.appendText(message + "\n");
        System.out.println("Message: " + message);
    }
    
    @Override
    public void showGameStats() {
        String stats = String.format("Game completed in %s with %d moves and %d hints!",
                getFormattedTime(), getMoveCount(), getHintsUsed());
        showMessage(stats);
    }
    
    @Override
    public void showHints(List<String> hints) {
        for (String hint : hints) {
            showMessage("Hint: " + hint);
        }
    }
    
    // Example event handlers for JavaFX
    /*
    @FXML
    private void handleNewGameEasy() {
        startNewGame(Difficulty.EASY);
    }
    
    @FXML
    private void handleNewGameMedium() {
        startNewGame(Difficulty.MEDIUM);
    }
    
    @FXML
    private void handleNewGameHard() {
        startNewGame(Difficulty.HARD);
    }
    
    @FXML
    private void handleNewGameExpert() {
        startNewGame(Difficulty.EXPERT);
    }
    
    @FXML
    private void handleGetHint() {
        getHint();
    }
    
    @FXML
    private void handleUndo() {
        undoMove();
    }
    
    @FXML
    private void handleSolve() {
        solvePuzzle();
    }
    
    @FXML
    private void handleReset() {
        resetGame();
    }
    
    @FXML
    private void handleValidate() {
        validateBoard();
    }
    
    private TextField getCellTextField(int row, int col) {
        // Return the TextField at the specified position in the GridPane
        return (TextField) boardGrid.getChildren().get(row * 9 + col);
    }
    
    private void setupCellEventHandlers() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int r = row, c = col;
                TextField cell = getCellTextField(row, col);
                
                cell.textProperty().addListener((obs, oldText, newText) -> {
                    if (newText.length() > 1) {
                        cell.setText(oldText);
                        return;
                    }
                    
                    if (newText.isEmpty()) {
                        makeMove(r, c, 0);
                    } else {
                        try {
                            int value = Integer.parseInt(newText);
                            if (value >= 1 && value <= 9) {
                                makeMove(r, c, value);
                            } else {
                                cell.setText(oldText);
                            }
                        } catch (NumberFormatException e) {
                            cell.setText(oldText);
                        }
                    }
                });
            }
        }
    }
    */
}