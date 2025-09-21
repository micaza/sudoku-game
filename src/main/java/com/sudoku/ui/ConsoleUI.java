package com.sudoku.ui;

import com.sudoku.manager.GameManager;
import com.sudoku.model.Difficulty;
import com.sudoku.model.SudokuBoard;
import java.util.Scanner;
import java.util.List;

public class ConsoleUI {
    private final GameManager gameManager;
    private final Scanner scanner;
    private boolean running;
    
    public ConsoleUI() {
        this.gameManager = new GameManager();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public void start() {
        printWelcome();
        
        while (running) {
            printMainMenu();
            handleMainMenuChoice();
        }
        
        scanner.close();
    }
    
    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║           SUDOKU GAME                ║");
        System.out.println("║      Welcome to Console Sudoku!     ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }
    
    private void printMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. New Game");
        System.out.println("2. Continue Current Game");
        System.out.println("3. Show Game Stats");
        System.out.println("4. Exit");
        System.out.print("Choose an option (1-4): ");
    }
    
    private void handleMainMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    if (gameManager.getCurrentBoard() != null) {
                        playGame();
                    } else {
                        System.out.println("No game in progress. Please start a new game.");
                    }
                    break;
                case 3:
                    showGameStats();
                    break;
                case 4:
                    running = false;
                    System.out.println("Thanks for playing Sudoku!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void startNewGame() {
        System.out.println("\n=== SELECT DIFFICULTY ===");
        System.out.println("1. Easy (40-45 clues)");
        System.out.println("2. Medium (30-35 clues)");
        System.out.println("3. Hard (20-25 clues)");
        System.out.println("4. Expert (17-22 clues)");
        System.out.print("Choose difficulty (1-4): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            Difficulty difficulty;
            
            switch (choice) {
                case 1: difficulty = Difficulty.EASY; break;
                case 2: difficulty = Difficulty.MEDIUM; break;
                case 3: difficulty = Difficulty.HARD; break;
                case 4: difficulty = Difficulty.EXPERT; break;
                default:
                    System.out.println("Invalid choice. Starting with Medium difficulty.");
                    difficulty = Difficulty.MEDIUM;
            }
            
            System.out.println("Generating puzzle... Please wait.");
            gameManager.startNewGame(difficulty);
            System.out.println("New " + difficulty.name().toLowerCase() + " game started!");
            playGame();
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Starting with Medium difficulty.");
            gameManager.startNewGame(Difficulty.MEDIUM);
            playGame();
        }
    }
    
    private void playGame() {
        while (!gameManager.isGameCompleted() && running) {
            printBoard();
            printGameMenu();
            handleGameMenuChoice();
        }
        
        if (gameManager.isGameCompleted()) {
            printBoard();
            System.out.println("\n🎉 CONGRATULATIONS! 🎉");
            System.out.println("You completed the puzzle!");
            showGameStats();
        }
    }
    
    private void printBoard() {
        System.out.println("\n" + gameManager.getFormattedTime() + " | Moves: " + 
                          gameManager.getMoveCount() + " | Hints: " + gameManager.getHintsUsed());
        System.out.println();
        
        SudokuBoard board = gameManager.getCurrentBoard();
        System.out.println("    1 2 3   4 5 6   7 8 9");
        System.out.println("  ┌───────┬───────┬───────┐");
        
        for (int row = 0; row < 9; row++) {
            if (row == 3 || row == 6) {
                System.out.println("  ├───────┼───────┼───────┤");
            }
            
            System.out.print((row + 1) + " │ ");
            
            for (int col = 0; col < 9; col++) {
                if (col == 3 || col == 6) {
                    System.out.print("│ ");
                }
                
                int value = board.getValue(row, col);
                if (value == 0) {
                    System.out.print(". ");
                } else {
                    // Highlight fixed cells differently
                    if (board.getCell(row, col).isFixed()) {
                        System.out.print(value + " ");
                    } else {
                        System.out.print(value + " ");
                    }
                }
            }
            System.out.println("│");
        }
        System.out.println("  └───────┴───────┴───────┘");
    }
    
    private void printGameMenu() {
        System.out.println("\n=== GAME MENU ===");
        System.out.println("1. Make Move (row col value)");
        System.out.println("2. Clear Cell (row col)");
        System.out.println("3. Get Hint");
        System.out.println("4. Undo Move");
        System.out.println("5. Solve Puzzle");
        System.out.println("6. Reset to Original");
        System.out.println("7. Validate Current State");
        System.out.println("8. Save Game");
        System.out.println("9. Load Game");
        System.out.println("10. Back to Main Menu");
        System.out.print("Choose an option: ");
    }
    
    private void handleGameMenuChoice() {
        try {
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            int choice = Integer.parseInt(parts[0]);
            
            switch (choice) {
                case 1:
                    handleMakeMove(parts);
                    break;
                case 2:
                    handleClearCell(parts);
                    break;
                case 3:
                    handleGetHint();
                    break;
                case 4:
                    handleUndoMove();
                    break;
                case 5:
                    handleSolvePuzzle();
                    break;
                case 6:
                    handleReset();
                    break;
                case 7:
                    handleValidate();
                    break;
                case 8:
                    handleSaveGame();
                    break;
                case 9:
                    if (handleLoadGame()) {
                        return;
                    }
                    break;
                case 10:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please try again.");
        }
    }
    
    private void handleMakeMove(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: 1 <row> <col> <value>");
            System.out.println("Example: 1 3 5 7 (places 7 in row 3, column 5)");
            return;
        }
        
        try {
            int row = Integer.parseInt(parts[1]) - 1; // Convert to 0-based
            int col = Integer.parseInt(parts[2]) - 1;
            int value = Integer.parseInt(parts[3]);
            
            if (gameManager.makeMove(row, col, value)) {
                System.out.println("Move successful!");
            } else {
                System.out.println("Invalid move. Check the position and value.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numbers. Please use format: 1 <row> <col> <value>");
        }
    }
    
    private void handleClearCell(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: 2 <row> <col>");
            return;
        }
        
        try {
            int row = Integer.parseInt(parts[1]) - 1;
            int col = Integer.parseInt(parts[2]) - 1;
            
            if (gameManager.makeMove(row, col, 0)) {
                System.out.println("Cell cleared!");
            } else {
                System.out.println("Cannot clear this cell.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numbers. Please use format: 2 <row> <col>");
        }
    }
    
    private void handleGetHint() {
        List<String> hints = gameManager.getHint();
        System.out.println("\n=== HINT ===");
        for (String hint : hints) {
            System.out.println(hint);
        }
    }
    
    private void handleUndoMove() {
        if (gameManager.undoMove()) {
            System.out.println("Move undone!");
        } else {
            System.out.println("No moves to undo.");
        }
    }
    
    private void handleSolvePuzzle() {
        System.out.print("Are you sure you want to solve the puzzle? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (gameManager.solvePuzzle()) {
                System.out.println("Puzzle solved!");
            } else {
                System.out.println("Unable to solve the puzzle.");
            }
        }
    }
    
    private void handleReset() {
        System.out.print("Are you sure you want to reset to the original puzzle? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            gameManager.resetToOriginal();
            System.out.println("Puzzle reset to original state!");
        }
    }
    
    private void handleValidate() {
        if (gameManager.validateCurrentState()) {
            System.out.println("✓ Current state is valid - no conflicts detected.");
        } else {
            System.out.println("✗ Current state has conflicts - check your entries.");
        }
    }
    
    private void showGameStats() {
        if (gameManager.getCurrentBoard() != null) {
            System.out.println("\n=== GAME STATISTICS ===");
            System.out.println(gameManager.getGameStats());
        } else {
            System.out.println("No game statistics available.");
        }
    }
    
    private void handleSaveGame() {
        System.out.print("\nEnter your name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Player";
        
        System.out.print("Enter filename (or press Enter for auto-generated): ");
        String filename = scanner.nextLine().trim();
        
        if (gameManager.saveGame(filename.isEmpty() ? null : filename, playerName)) {
            System.out.println("Game saved successfully!");
        } else {
            System.out.println("Failed to save game.");
        }
    }
    
    private boolean handleLoadGame() {
        List<String> savedGames = gameManager.listSavedGames();
        
        if (savedGames.isEmpty()) {
            System.out.println("\nNo saved games found.");
            return false;
        }
        
        System.out.println("\nSaved games:");
        for (int i = 0; i < savedGames.size(); i++) {
            System.out.println((i + 1) + ". " + savedGames.get(i));
        }
        
        System.out.print("\nEnter game number to load (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return false;
            
            if (choice > 0 && choice <= savedGames.size()) {
                String filename = savedGames.get(choice - 1);
                if (gameManager.loadGame(filename)) {
                    System.out.println("Game loaded successfully!");
                    return true;
                } else {
                    System.out.println("Failed to load game.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
        return false;
    }
}