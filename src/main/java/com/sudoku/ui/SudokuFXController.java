package com.sudoku.ui;

import com.sudoku.manager.GameManager;
import com.sudoku.model.Difficulty;
import com.sudoku.model.SudokuBoard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SudokuFXController implements Initializable {
    @FXML private GridPane boardGrid;
    @FXML private Label timeLabel;
    @FXML private Label movesLabel;
    @FXML private Label hintsLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea messageArea;
    @FXML private Button newGameButton;
    @FXML private Button solveButton;
    @FXML private Button checkButton;
    @FXML private Button resetButton;
    @FXML private Button hintButton;
    @FXML private Button undoButton;
    @FXML private ComboBox<Difficulty> difficultyCombo;
    
    private GameManager gameManager;
    private SudokuCell[][] cells;
    private Timeline gameTimer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameManager = new GameManager();
        cells = new SudokuCell[9][9];
        
        setupDifficultyCombo();
        setupBoard();
        setupTimer();
        updateUI();
    }
    
    private void setupDifficultyCombo() {
        difficultyCombo.getItems().addAll(Difficulty.values());
        difficultyCombo.setValue(Difficulty.MEDIUM);
    }
    
    private void setupBoard() {
        boardGrid.getChildren().clear();
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = new SudokuCell(row, col, this);
                cells[row][col] = cell;
                
                // Add thick borders for 3x3 box separation
                String style = "-fx-border-color: #333333; -fx-border-width: ";
                style += (row % 3 == 0 ? "2 " : "1 "); // top
                style += (col % 3 == 2 ? "2 " : "1 "); // right
                style += (row % 3 == 2 ? "2 " : "1 "); // bottom
                style += (col % 3 == 0 ? "2" : "1");   // left
                
                cell.setStyle(cell.getStyle() + "; " + style);
                
                boardGrid.add(cell, col, row);
            }
        }
    }
    
    private void setupTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
    }
    
    private void updateTimer() {
        if (gameManager.getCurrentBoard() != null && !gameManager.isGameCompleted()) {
            Platform.runLater(() -> timeLabel.setText("Time: " + gameManager.getFormattedTime()));
        }
    }
    
    @FXML
    private void handleNewGame() {
        Difficulty difficulty = difficultyCombo.getValue();
        
        showMessage("Generating " + difficulty.name().toLowerCase() + " puzzle...");
        
        // Run generation in background thread
        new Thread(() -> {
            gameManager.startNewGame(difficulty);
            Platform.runLater(() -> {
                updateBoard();
                updateUI();
                gameTimer.play();
                showMessage("New " + difficulty.name().toLowerCase() + " game started!");
            });
        }).start();
    }
    
    @FXML
    private void handleSolve() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Solve Puzzle");
        alert.setHeaderText("Are you sure you want to solve the puzzle?");
        alert.setContentText("This will complete the current puzzle automatically.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (gameManager.solvePuzzle()) {
                    updateBoard();
                    updateUI();
                    gameTimer.stop();
                    showMessage("Puzzle solved!");
                } else {
                    showMessage("Unable to solve puzzle.");
                }
            }
        });
    }
    
    @FXML
    private void handleCheck() {
        if (gameManager.validateCurrentState()) {
            showMessage("✓ Board is valid - no conflicts detected.");
        } else {
            showMessage("✗ Board has conflicts - check your entries.");
            highlightErrors();
        }
    }
    
    @FXML
    private void handleReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Game");
        alert.setHeaderText("Are you sure you want to reset?");
        alert.setContentText("This will clear all your progress.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                gameManager.resetToOriginal();
                updateBoard();
                updateUI();
                showMessage("Game reset to original state.");
            }
        });
    }
    
    @FXML
    private void handleHint() {
        List<String> hints = gameManager.getHint();
        StringBuilder hintText = new StringBuilder("Hints:\n");
        for (String hint : hints) {
            hintText.append("• ").append(hint).append("\n");
        }
        showMessage(hintText.toString());
    }
    
    @FXML
    private void handleUndo() {
        if (gameManager.undoMove()) {
            updateBoard();
            updateUI();
            showMessage("Move undone.");
        } else {
            showMessage("No moves to undo.");
        }
    }
    
    public boolean makeMove(int row, int col, int value) {
        boolean success = gameManager.makeMove(row, col, value);
        
        if (success) {
            updateUI();
            
            if (gameManager.isGameCompleted()) {
                gameTimer.stop();
                showCompletionDialog();
            }
        }
        
        return success;
    }
    
    public void highlightRelated(int row, int col) {
        clearHighlights();
        
        // Highlight row, column, and 3x3 box
        for (int i = 0; i < 9; i++) {
            cells[row][i].setHighlight(true); // Row
            cells[i][col].setHighlight(true); // Column
        }
        
        // 3x3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                cells[r][c].setHighlight(true);
            }
        }
    }
    
    public void clearHighlights() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setHighlight(false);
                cells[row][col].setError(false);
            }
        }
    }
    
    private void highlightErrors() {
        SudokuBoard board = gameManager.getCurrentBoard();
        if (board == null) return;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!board.isEmpty(row, col) && board.hasConflict(row, col)) {
                    cells[row][col].setError(true);
                }
            }
        }
    }
    
    private void updateBoard() {
        SudokuBoard board = gameManager.getCurrentBoard();
        if (board == null) return;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getValue(row, col);
                boolean fixed = board.getCell(row, col).isFixed();
                cells[row][col].setValue(value, fixed);
            }
        }
    }
    
    private void updateUI() {
        if (gameManager.getCurrentBoard() != null) {
            movesLabel.setText("Moves: " + gameManager.getMoveCount());
            hintsLabel.setText("Hints: " + gameManager.getHintsUsed());
            
            if (gameManager.isGameCompleted()) {
                statusLabel.setText("Status: Completed!");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                statusLabel.setText("Status: In Progress");
                statusLabel.setStyle("-fx-text-fill: blue;");
            }
        }
    }
    
    private void showMessage(String message) {
        Platform.runLater(() -> {
            messageArea.appendText(message + "\n");
            messageArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    private void showCompletionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("Puzzle Completed!");
        alert.setContentText(String.format(
            "You completed the puzzle in %s with %d moves and %d hints!",
            gameManager.getFormattedTime(),
            gameManager.getMoveCount(),
            gameManager.getHintsUsed()
        ));
        
        alert.showAndWait();
        showMessage("🎉 Puzzle completed! " + gameManager.getGameStats());
    }
}