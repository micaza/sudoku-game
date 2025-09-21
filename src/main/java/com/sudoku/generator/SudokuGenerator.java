package com.sudoku.generator;

import com.sudoku.model.SudokuBoard;
import com.sudoku.model.Difficulty;
import com.sudoku.solver.SudokuSolver;
import java.util.*;

public class SudokuGenerator {
    private final SudokuSolver solver;
    private final Random random;
    
    public SudokuGenerator() {
        this.solver = new SudokuSolver();
        this.random = new Random();
    }
    
    public SudokuBoard generatePuzzle(Difficulty difficulty) {
        SudokuBoard completeBoard = generateCompleteBoard();
        return createPuzzle(completeBoard, difficulty);
    }
    
    private SudokuBoard generateCompleteBoard() {
        SudokuBoard board = new SudokuBoard();
        
        // Fill diagonal boxes first (they don't interfere with each other)
        fillDiagonalBoxes(board);
        
        // Fill remaining cells
        solver.solve(board);
        
        return board;
    }
    
    private void fillDiagonalBoxes(SudokuBoard board) {
        for (int box = 0; box < 3; box++) {
            fillBox(board, box * 3, box * 3);
        }
    }
    
    private void fillBox(SudokuBoard board, int startRow, int startCol) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(numbers, random);
        
        int index = 0;
        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = startCol; col < startCol + 3; col++) {
                board.setValue(row, col, numbers.get(index++));
            }
        }
    }
    
    private SudokuBoard createPuzzle(SudokuBoard completeBoard, Difficulty difficulty) {
        SudokuBoard puzzle = completeBoard.copy();
        int targetClues = difficulty.getRandomClueCount();
        
        // Get all cell positions
        List<int[]> positions = getAllPositions();
        Collections.shuffle(positions, random);
        
        int currentClues = 81; // Start with complete board
        
        for (int[] pos : positions) {
            if (currentClues <= targetClues) {
                break;
            }
            
            int row = pos[0];
            int col = pos[1];
            int originalValue = puzzle.getValue(row, col);
            
            // Temporarily remove the value
            puzzle.setValue(row, col, 0);
            
            // Check if puzzle still has unique solution
            if (solver.hasUniqueSolution(puzzle)) {
                currentClues--;
                puzzle.getCell(row, col).setFixed(false);
            } else {
                // Restore the value if removing it creates multiple solutions
                puzzle.setValue(row, col, originalValue);
                puzzle.getCell(row, col).setFixed(true);
            }
        }
        
        // Mark remaining filled cells as fixed
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (!puzzle.isEmpty(row, col)) {
                    puzzle.getCell(row, col).setFixed(true);
                }
            }
        }
        
        return puzzle;
    }
    
    private List<int[]> getAllPositions() {
        List<int[]> positions = new ArrayList<>();
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                positions.add(new int[]{row, col});
            }
        }
        return positions;
    }
    
    public SudokuBoard generateFromPattern(int[][] pattern) {
        SudokuBoard board = new SudokuBoard(pattern);
        if (solver.solve(board.copy())) {
            return board;
        }
        return null; // Invalid pattern
    }
    
    public boolean isValidPuzzle(SudokuBoard puzzle) {
        return solver.isValid(puzzle) && solver.hasUniqueSolution(puzzle);
    }
}