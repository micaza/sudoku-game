package com.sudoku.model;

public class Cell {
    private int value;
    private boolean isFixed;
    private boolean[] candidates;
    
    public Cell() {
        this.value = 0;
        this.isFixed = false;
        this.candidates = new boolean[10]; // Index 1-9 for candidates
    }
    
    public Cell(int value, boolean isFixed) {
        this();
        this.value = value;
        this.isFixed = isFixed;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        if (!isFixed && value >= 0 && value <= 9) {
            this.value = value;
        }
    }
    
    public boolean isFixed() {
        return isFixed;
    }
    
    public void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }
    
    public boolean isEmpty() {
        return value == 0;
    }
    
    public boolean[] getCandidates() {
        return candidates.clone();
    }
    
    public void setCandidate(int num, boolean possible) {
        if (num >= 1 && num <= 9) {
            candidates[num] = possible;
        }
    }
    
    public boolean isCandidate(int num) {
        return num >= 1 && num <= 9 && candidates[num];
    }
    
    @Override
    public String toString() {
        return isEmpty() ? "." : String.valueOf(value);
    }
}