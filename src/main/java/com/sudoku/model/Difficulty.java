package com.sudoku.model;

public enum Difficulty {
    EASY(40, 45),
    MEDIUM(30, 35),
    HARD(20, 25),
    EXPERT(17, 22);
    
    private final int minClues;
    private final int maxClues;
    
    Difficulty(int minClues, int maxClues) {
        this.minClues = minClues;
        this.maxClues = maxClues;
    }
    
    public int getMinClues() {
        return minClues;
    }
    
    public int getMaxClues() {
        return maxClues;
    }
    
    public int getRandomClueCount() {
        return minClues + (int) (Math.random() * (maxClues - minClues + 1));
    }
}