package com.sudoku.solver;

import com.sudoku.model.SudokuBoard;
import java.util.*;

public class SudokuSolver {
    
    public boolean solve(SudokuBoard board) {
        return solveBacktrack(board);
    }
    
    private boolean solveBacktrack(SudokuBoard board) {
        int[] emptyCell = findEmptyCell(board);
        if (emptyCell == null) {
            return true; // Board is complete
        }
        
        int row = emptyCell[0];
        int col = emptyCell[1];
        
        List<Integer> candidates = getShuffledCandidates();
        
        for (int num : candidates) {
            if (board.isValidMove(row, col, num)) {
                board.setValue(row, col, num);
                
                if (solveBacktrack(board)) {
                    return true;
                }
                
                board.setValue(row, col, 0); // Backtrack
            }
        }
        
        return false;
    }
    
    public boolean isValid(SudokuBoard board) {
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (!board.isEmpty(row, col) && board.hasConflict(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean hasUniqueSolution(SudokuBoard board) {
        SudokuBoard copy = board.copy();
        return countSolutions(copy, 2) == 1;
    }
    
    public int countSolutions(SudokuBoard board, int maxSolutions) {
        return countSolutionsPrivate(board, maxSolutions);
    }
    
    private int countSolutionsPrivate(SudokuBoard board, int maxSolutions) {
        int[] emptyCell = findEmptyCell(board);
        if (emptyCell == null) {
            return 1; // Found one solution
        }
        
        int row = emptyCell[0];
        int col = emptyCell[1];
        int solutionCount = 0;
        
        for (int num = 1; num <= 9; num++) {
            if (board.isValidMove(row, col, num)) {
                board.setValue(row, col, num);
                
                solutionCount += countSolutionsPrivate(board, maxSolutions - solutionCount);
                
                if (solutionCount >= maxSolutions) {
                    board.setValue(row, col, 0);
                    return solutionCount;
                }
                
                board.setValue(row, col, 0);
            }
        }
        
        return solutionCount;
    }
    
    private int[] findEmptyCell(SudokuBoard board) {
        // Find empty cell with minimum remaining values (MRV heuristic)
        int minCandidates = 10;
        int[] bestCell = null;
        
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    int candidateCount = countCandidates(board, row, col);
                    if (candidateCount < minCandidates) {
                        minCandidates = candidateCount;
                        bestCell = new int[]{row, col};
                        if (candidateCount == 1) {
                            return bestCell; // Best possible case
                        }
                    }
                }
            }
        }
        
        return bestCell;
    }
    
    private int countCandidates(SudokuBoard board, int row, int col) {
        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (board.isValidMove(row, col, num)) {
                count++;
            }
        }
        return count;
    }
    
    private List<Integer> getShuffledCandidates() {
        List<Integer> candidates = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(candidates);
        return candidates;
    }
    
    public List<String> getHint(SudokuBoard board) {
        List<String> hints = new ArrayList<>();
        
        // Find cells with only one possible value
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    List<Integer> possibleValues = new ArrayList<>();
                    for (int num = 1; num <= 9; num++) {
                        if (board.isValidMove(row, col, num)) {
                            possibleValues.add(num);
                        }
                    }
                    
                    if (possibleValues.size() == 1) {
                        hints.add(String.format("Cell (%d,%d) can only be %d", 
                                row + 1, col + 1, possibleValues.get(0)));
                    }
                }
            }
        }
        
        return hints;
    }
}