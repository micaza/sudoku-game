package com.sudoku.ui;

import com.sudoku.manager.GameManager;
import com.sudoku.model.Difficulty;
import com.sudoku.model.SudokuBoard;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class SudokuSwingGUI extends JFrame {
    private GameManager gameManager;
    private JTextField[][] cells;
    private SudokuDocumentFilter[][] filters;
    private JLabel timeLabel, movesLabel, hintsLabel, statusLabel;
    private JTextArea messageArea;
    private JComboBox<Difficulty> difficultyCombo;
    private Timer gameTimer;
    private JButton finishInputButton;
    
    public SudokuSwingGUI() {
        gameManager = new GameManager();
        cells = new JTextField[9][9];
        filters = new SudokuDocumentFilter[9][9];
        
        initializeGUI();
        setupTimer();
    }
    
    private void initializeGUI() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout());
        header.setBackground(new Color(74, 144, 226));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("SUDOKU GAME");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        
        return header;
    }
    
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        
        // Left side - game board
        JPanel boardPanel = createBoardPanel();
        
        // Right side - controls and messages
        JPanel rightPanel = createRightPanel();
        
        main.add(boardPanel, BorderLayout.CENTER);
        main.add(rightPanel, BorderLayout.EAST);
        
        return main;
    }
    
    private JPanel createBoardPanel() {
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Controls above board
        JPanel controls = new JPanel(new FlowLayout());
        
        difficultyCombo = new JComboBox<>(Difficulty.values());
        difficultyCombo.setSelectedItem(Difficulty.MEDIUM);
        controls.add(new JLabel("Difficulty:"));
        controls.add(difficultyCombo);
        
        JButton newGameBtn = createButton("New Game", this::handleNewGame);
        JButton finishInputBtn = createButton("Finish Input", this::handleFinishCustomInput);
        JButton solveBtn = createButton("Solve", this::handleSolve);
        JButton checkBtn = createButton("Check", this::handleCheck);
        JButton resetBtn = createButton("Reset", this::handleReset);
        JButton hintBtn = createButton("Hint", this::handleHint);
        JButton undoBtn = createButton("Undo", this::handleUndo);
        JButton saveBtn = createButton("Save", this::handleSave);
        JButton loadBtn = createButton("Load", this::handleLoad);
        
        controls.add(newGameBtn);
        controls.add(finishInputBtn);
        controls.add(solveBtn);
        controls.add(checkBtn);
        controls.add(resetBtn);
        controls.add(hintBtn);
        controls.add(undoBtn);
        controls.add(saveBtn);
        controls.add(loadBtn);
        
        // Store reference to finish input button for visibility control
        this.finishInputButton = finishInputBtn;
        finishInputBtn.setVisible(false);
        
        // Sudoku grid
        JPanel gridPanel = createSudokuGrid();
        
        boardContainer.add(controls, BorderLayout.NORTH);
        boardContainer.add(gridPanel, BorderLayout.CENTER);
        
        return boardContainer;
    }
    
    private JPanel createSudokuGrid() {
        JPanel grid = new JPanel(new GridLayout(9, 9, 1, 1));
        grid.setBackground(Color.BLACK);
        grid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField cell = createCell(row, col);
                cells[row][col] = cell;
                grid.add(cell);
            }
        }
        
        return grid;
    }
    
    private JTextField createCell(int row, int col) {
        JTextField cell = new JTextField();
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial", Font.BOLD, 18));
        cell.setPreferredSize(new Dimension(50, 50));
        
        // Create thick borders for 3x3 boxes
        Border border = createCellBorder(row, col);
        cell.setBorder(border);
        
        // Add input filter
        SudokuDocumentFilter filter = new SudokuDocumentFilter(row, col);
        filters[row][col] = filter;
        ((AbstractDocument) cell.getDocument()).setDocumentFilter(filter);
        
        // Add focus listener for highlighting
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                highlightRelated(row, col);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                clearHighlights();
            }
        });
        
        return cell;
    }
    
    private Border createCellBorder(int row, int col) {
        int top = (row % 3 == 0) ? 3 : 1;
        int left = (col % 3 == 0) ? 3 : 1;
        int bottom = (row % 3 == 2) ? 3 : 1;
        int right = (col % 3 == 2) ? 3 : 1;
        
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }
    
    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(250, 400));
        right.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        
        JLabel messageLabel = new JLabel("Messages:");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        messageArea = new JTextArea(20, 20);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        messageArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        
        right.add(messageLabel, BorderLayout.NORTH);
        right.add(scrollPane, BorderLayout.CENTER);
        
        return right;
    }
    
    private JPanel createStatusPanel() {
        JPanel status = new JPanel(new FlowLayout());
        status.setBorder(BorderFactory.createEtchedBorder());
        
        timeLabel = new JLabel("Time: 00:00");
        movesLabel = new JLabel("Moves: 0");
        hintsLabel = new JLabel("Hints: 0");
        statusLabel = new JLabel("Status: Ready");
        
        Font statusFont = new Font("Arial", Font.BOLD, 12);
        timeLabel.setFont(statusFont);
        movesLabel.setFont(statusFont);
        hintsLabel.setFont(statusFont);
        statusLabel.setFont(statusFont);
        
        status.add(timeLabel);
        status.add(Box.createHorizontalStrut(20));
        status.add(movesLabel);
        status.add(Box.createHorizontalStrut(20));
        status.add(hintsLabel);
        status.add(Box.createHorizontalStrut(20));
        status.add(statusLabel);
        
        return status;
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        button.setFocusPainted(false);
        return button;
    }
    
    private void setupTimer() {
        gameTimer = new Timer(1000, e -> updateTimer());
    }
    
    private void updateTimer() {
        if (gameManager.getCurrentBoard() != null && !gameManager.isGameCompleted()) {
            timeLabel.setText("Time: " + gameManager.getFormattedTime());
        }
    }
    
    // Event handlers
    private void handleNewGame() {
        String[] options = {"Traditional Game", "Custom Puzzle"};
        int choice = JOptionPane.showOptionDialog(this,
            "Choose game type:",
            "New Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Traditional game
            Difficulty difficulty = (Difficulty) difficultyCombo.getSelectedItem();
            showMessage("Generating " + difficulty.name().toLowerCase() + " puzzle...");
            
            new Thread(() -> {
                try {
                    gameManager.startNewGame(difficulty);
                    SwingUtilities.invokeLater(() -> {
                        updateBoard();
                        updateUI();
                        gameTimer.start();
                        showMessage("New " + difficulty.name().toLowerCase() + " game started!");
                    });
                } catch (Exception e) {
                    showMessage("ERROR generating puzzle: " + e.getMessage());
                }
            }).start();
        } else if (choice == 1) {
            // Custom puzzle
            handleCustomPuzzleInput();
        }
    }
    
    private void handleSolve() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to solve the puzzle?",
            "Solve Puzzle", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (gameManager.solvePuzzle()) {
                updateBoard();
                updateUI();
                gameTimer.stop();
                showMessage("Puzzle solved!");
            } else {
                showMessage("Unable to solve puzzle.");
            }
        }
    }
    
    private void handleCheck() {
        if (gameManager.validateCurrentState()) {
            showMessage("✓ Board is valid - no conflicts detected.");
        } else {
            showMessage("✗ Board has conflicts - check your entries.");
            highlightErrors();
        }
    }
    
    private void handleReset() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset the game?",
            "Reset Game", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            gameManager.resetToOriginal();
            updateBoard();
            updateUI();
            showMessage("Game reset to original state.");
        }
    }
    
    private void handleHint() {
        List<String> hints = gameManager.getHint();
        StringBuilder hintText = new StringBuilder("Hints:\n");
        for (String hint : hints) {
            hintText.append("• ").append(hint).append("\n");
        }
        showMessage(hintText.toString());
    }
    
    private void handleUndo() {
        if (gameManager.undoMove()) {
            updateBoard();
            updateUI();
            showMessage("Move undone.");
        } else {
            showMessage("No moves to undo.");
        }
    }
    
    private void handleSave() {
        if (gameManager.getCurrentBoard() == null) {
            showMessage("No game to save.");
            return;
        }
        
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Save Game", JOptionPane.QUESTION_MESSAGE);
        if (playerName == null) return; // User cancelled
        if (playerName.trim().isEmpty()) playerName = "Player";
        
        String filename = JOptionPane.showInputDialog(this, "Enter filename (or leave empty for auto-generated):", "Save Game", JOptionPane.QUESTION_MESSAGE);
        if (filename == null) return; // User cancelled
        
        if (gameManager.saveGame(filename.trim().isEmpty() ? null : filename, playerName)) {
            showMessage("Game saved successfully!");
        } else {
            showMessage("Failed to save game.");
            JOptionPane.showMessageDialog(this, "Failed to save game.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLoad() {
        java.util.List<String> savedGames = gameManager.listSavedGames();
        
        if (savedGames.isEmpty()) {
            showMessage("No saved games found.");
            JOptionPane.showMessageDialog(this, "No saved games found.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] gameArray = savedGames.toArray(new String[0]);
        String selectedGame = (String) JOptionPane.showInputDialog(this,
            "Select a game to load:",
            "Load Game",
            JOptionPane.QUESTION_MESSAGE,
            null,
            gameArray,
            gameArray[0]);
        
        if (selectedGame != null) {
            if (gameManager.loadGame(selectedGame)) {
                updateBoard();
                updateUI();
                gameTimer.start();
                showMessage("Game loaded successfully: " + selectedGame);
            } else {
                showMessage("Failed to load game: " + selectedGame);
                JOptionPane.showMessageDialog(this, "Failed to load game.", "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean makeMove(int row, int col, int value) {
        boolean success = gameManager.makeMove(row, col, value);
        
        if (success) {
            updateUI();
            
            if (gameManager.isGameCompleted()) {
                gameTimer.stop();
                showCompletionDialog();
            }
        } else {
            // Flash cell red for error
            JTextField cell = cells[row][col];
            Color original = cell.getBackground();
            cell.setBackground(Color.RED);
            Timer flashTimer = new Timer(200, e -> cell.setBackground(original));
            flashTimer.setRepeats(false);
            flashTimer.start();
        }
        
        return success;
    }
    
    private void highlightRelated(int row, int col) {
        clearHighlights();
        
        Color highlightColor = new Color(255, 243, 224);
        
        // Highlight row, column, and 3x3 box
        for (int i = 0; i < 9; i++) {
            cells[row][i].setBackground(highlightColor);
            cells[i][col].setBackground(highlightColor);
        }
        
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                cells[r][c].setBackground(highlightColor);
            }
        }
    }
    
    private void clearHighlights() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setBackground(Color.WHITE);
            }
        }
    }
    
    private void highlightErrors() {
        SudokuBoard board = gameManager.getCurrentBoard();
        if (board == null) return;
        
        Color errorColor = new Color(255, 235, 238);
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!board.isEmpty(row, col) && board.hasConflict(row, col)) {
                    cells[row][col].setBackground(errorColor);
                }
            }
        }
    }
    
    private void updateBoard() {
        SudokuBoard board = gameManager.getCurrentBoard();
        if (board == null) {
            showMessage("updateBoard: board is null!");
            return;
        }
        
        showMessage("Updating board display...");
        int updatedCells = 0;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField cell = cells[row][col];
                SudokuDocumentFilter filter = filters[row][col];
                int value = board.getValue(row, col);
                boolean fixed = board.getCell(row, col).isFixed();
                
                // Enable programmatic updates
                filter.setProgrammaticUpdate(true);
                
                if (value == 0) {
                    cell.setText("");
                } else {
                    cell.setText(String.valueOf(value));
                    updatedCells++;
                }
                
                // Disable programmatic updates
                filter.setProgrammaticUpdate(false);
                
                // Style cells based on mode and state
                if (gameManager.isCustomInputMode()) {
                    cell.setBackground(new Color(255, 248, 225)); // Light yellow for input mode
                    cell.setForeground(new Color(255, 87, 34)); // Orange text
                    cell.setEditable(true);
                } else if (fixed) {
                    cell.setBackground(new Color(240, 240, 240));
                    cell.setForeground(Color.BLACK);
                    cell.setEditable(false);
                } else {
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(new Color(33, 150, 243));
                    cell.setEditable(true);
                }
                
                cell.repaint();
            }
        }
        
        showMessage("Updated " + updatedCells + " cells with values");
        
        // Force repaint of the entire grid
        this.repaint();
    }
    
    private void updateUI() {
        if (gameManager.getCurrentBoard() != null) {
            movesLabel.setText("Moves: " + gameManager.getMoveCount());
            hintsLabel.setText("Hints: " + gameManager.getHintsUsed());
            
            if (gameManager.isGameCompleted()) {
                statusLabel.setText("Status: Completed!");
                statusLabel.setForeground(new Color(76, 175, 80));
            } else {
                statusLabel.setText("Status: In Progress");
                statusLabel.setForeground(new Color(33, 150, 243));
            }
        }
    }
    
    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }
    
    private void showCompletionDialog() {
        String message = String.format(
            "Congratulations! You completed the puzzle in %s with %d moves and %d hints!",
            gameManager.getFormattedTime(),
            gameManager.getMoveCount(),
            gameManager.getHintsUsed()
        );
        
        JOptionPane.showMessageDialog(this, message, "Puzzle Completed!", 
            JOptionPane.INFORMATION_MESSAGE);
        showMessage("🎉 Puzzle completed! " + gameManager.getGameStats());
    }
    
    private void handleCustomPuzzleInput() {
        gameManager.startCustomInputMode();
        updateBoard();
        updateUI();
        statusLabel.setText("Status: Enter your puzzle");
        statusLabel.setForeground(new Color(255, 152, 0));
        finishInputButton.setVisible(true);
        showMessage("Custom puzzle input mode activated. Click cells to enter numbers.");
        
        JOptionPane.showMessageDialog(this,
            "Enter your puzzle by clicking on cells and typing numbers.\n" +
            "Leave cells empty for unknowns.\n" +
            "Click 'Finish Input' when done.",
            "Custom Puzzle Input",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleFinishCustomInput() {
        GameManager.CustomPuzzleResult result = gameManager.finishCustomInput();
        
        if (result.isSuccess()) {
            finishInputButton.setVisible(false);
            updateBoard();
            updateUI();
            showMessage("Custom puzzle validated: " + result.getMessage());
            
            int choice = JOptionPane.showConfirmDialog(this,
                "Solve this puzzle now?",
                "Solve Custom Puzzle", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                if (gameManager.solveCustomPuzzle()) {
                    updateBoard();
                    updateUI();
                    showMessage("Custom puzzle solved!");
                    JOptionPane.showMessageDialog(this, "Puzzle solved successfully!", "Solution Found", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Failed to solve custom puzzle.");
                }
            }
        } else {
            showMessage("Custom puzzle error: " + result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Invalid Puzzle", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Document filter for input validation
    private class SudokuDocumentFilter extends DocumentFilter {
        private final int row, col;
        private boolean programmaticUpdate = false;
        
        public SudokuDocumentFilter(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        public void setProgrammaticUpdate(boolean programmatic) {
            this.programmaticUpdate = programmatic;
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            
            // Allow programmatic updates without validation
            if (programmaticUpdate) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            
            // User input validation
            if (text.isEmpty()) {
                super.replace(fb, offset, length, text, attrs);
                makeMove(row, col, 0);
                return;
            }
            
            if (text.matches("[1-9]") && text.length() == 1) {
                super.replace(fb, offset, length, text, attrs);
                int value = Integer.parseInt(text);
                if (!makeMove(row, col, value)) {
                    // Revert if invalid move
                    super.replace(fb, 0, fb.getDocument().getLength(), "", attrs);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SudokuSwingGUI().setVisible(true);
        });
    }
}