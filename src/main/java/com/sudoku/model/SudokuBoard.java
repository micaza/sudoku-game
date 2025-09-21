package com.sudoku.model;

public class SudokuBoard {
    public static final int SIZE = 9;
    public static final int BOX_SIZE = 3;
    
    private Cell[][] board;
    
    public SudokuBoard() {
        board = new Cell[SIZE][SIZE];
        initializeBoard();
    }
    
    public SudokuBoard(int[][] values) {
        this();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (values[row][col] != 0) {
                    board[row][col].setValue(values[row][col]);
                    board[row][col].setFixed(true);
                }
            }
        }
    }
    
    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = new Cell();
            }
        }
    }
    
    public Cell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }
    
    public int getValue(int row, int col) {
        Cell cell = getCell(row, col);
        return cell != null ? cell.getValue() : 0;
    }
    
    public void setValue(int row, int col, int value) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            cell.setValue(value);
        }
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
    
    public boolean isValidMove(int row, int col, int value) {
        if (!isValidPosition(row, col) || value < 1 || value > 9) {
            return false;
        }
        
        // Check if cell is already fixed
        if (board[row][col].isFixed()) {
            return false;
        }
        
        // Temporarily place the value to check validity
        int originalValue = board[row][col].getValue();
        board[row][col].setValue(value);
        
        boolean valid = !hasConflict(row, col);
        
        // Restore original value
        board[row][col].setValue(originalValue);
        
        return valid;
    }
    
    public boolean hasConflict(int row, int col) {
        int value = getValue(row, col);
        if (value == 0) return false;
        
        return hasRowConflict(row, col, value) || 
               hasColumnConflict(row, col, value) || 
               hasBoxConflict(row, col, value);
    }
    
    private boolean hasRowConflict(int row, int col, int value) {
        for (int c = 0; c < SIZE; c++) {
            if (c != col && getValue(row, c) == value) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasColumnConflict(int row, int col, int value) {
        for (int r = 0; r < SIZE; r++) {
            if (r != row && getValue(r, col) == value) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasBoxConflict(int row, int col, int value) {
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        
        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if ((r != row || c != col) && getValue(r, c) == value) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isComplete() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col].isEmpty() || hasConflict(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isEmpty(int row, int col) {
        return getValue(row, col) == 0;
    }
    
    public void clear() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!board[row][col].isFixed()) {
                    board[row][col].setValue(0);
                }
            }
        }
    }
    
    public SudokuBoard copy() {
        SudokuBoard copy = new SudokuBoard();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell original = this.board[row][col];
                copy.board[row][col] = new Cell(original.getValue(), original.isFixed());
            }
        }
        return copy;
    }
    
    public SudokuBoard(int[][] values, boolean[][] fixed) {
        board = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new Cell(values[i][j], fixed[i][j]);
            }
        }
    }
    
    public int[][] getBoardArray() {
        int[][] array = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                array[i][j] = board[i][j].getValue();
            }
        }
        return array;
    }
    
    public boolean[][] getFixedCellsArray() {
        boolean[][] array = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                array[i][j] = board[i][j].isFixed();
            }
        }
        return array;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            if (row % BOX_SIZE == 0 && row != 0) {
                sb.append("------+-------+------\n");
            }
            for (int col = 0; col < SIZE; col++) {
                if (col % BOX_SIZE == 0 && col != 0) {
                    sb.append("| ");
                }
                sb.append(board[row][col]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}