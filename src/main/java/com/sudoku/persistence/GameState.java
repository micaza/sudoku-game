package com.sudoku.persistence;

import com.sudoku.model.Difficulty;

public class GameState {
    private int[][] currentBoard;
    private int[][] originalBoard;
    private boolean[][] fixedCells;
    private Difficulty difficulty;
    private long startTime;
    private boolean gameCompleted;
    private int hintsUsed;
    private String playerName;
    private long saveTime;
    
    public GameState() {}
    
    public GameState(int[][] currentBoard, int[][] originalBoard, boolean[][] fixedCells,
                    Difficulty difficulty, long startTime, boolean gameCompleted, 
                    int hintsUsed, String playerName) {
        this.currentBoard = currentBoard;
        this.originalBoard = originalBoard;
        this.fixedCells = fixedCells;
        this.difficulty = difficulty;
        this.startTime = startTime;
        this.gameCompleted = gameCompleted;
        this.hintsUsed = hintsUsed;
        this.playerName = playerName;
        this.saveTime = System.currentTimeMillis();
    }
    
    // Getters and setters
    public int[][] getCurrentBoard() { return currentBoard; }
    public void setCurrentBoard(int[][] currentBoard) { this.currentBoard = currentBoard; }
    
    public int[][] getOriginalBoard() { return originalBoard; }
    public void setOriginalBoard(int[][] originalBoard) { this.originalBoard = originalBoard; }
    
    public boolean[][] getFixedCells() { return fixedCells; }
    public void setFixedCells(boolean[][] fixedCells) { this.fixedCells = fixedCells; }
    
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public boolean isGameCompleted() { return gameCompleted; }
    public void setGameCompleted(boolean gameCompleted) { this.gameCompleted = gameCompleted; }
    
    public int getHintsUsed() { return hintsUsed; }
    public void setHintsUsed(int hintsUsed) { this.hintsUsed = hintsUsed; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public long getSaveTime() { return saveTime; }
    public void setSaveTime(long saveTime) { this.saveTime = saveTime; }
}