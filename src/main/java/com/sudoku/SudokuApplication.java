package com.sudoku;

import com.sudoku.ui.ConsoleUI;

/**
 * Main application class for the Sudoku game.
 * 
 * This application provides:
 * - Multiple difficulty levels (Easy, Medium, Hard, Expert)
 * - Puzzle generation with guaranteed unique solutions
 * - Backtracking solver with optimization
 * - Game management with move history and statistics
 * - Console-based user interface
 * 
 * To run: java com.sudoku.SudokuApplication
 */
public class SudokuApplication {
    
    public static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.err.println("An error occurred while running the Sudoku game:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}