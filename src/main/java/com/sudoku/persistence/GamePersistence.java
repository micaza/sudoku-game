package com.sudoku.persistence;

import com.sudoku.model.SudokuBoard;
import com.sudoku.model.Difficulty;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class GamePersistence {
    private static final String SAVE_DIR = "saved_games";
    private static final String FILE_EXTENSION = ".sudoku";
    
    public GamePersistence() {
        createSaveDirectory();
    }
    
    private void createSaveDirectory() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create save directory: " + e.getMessage());
        }
    }
    
    public boolean saveGame(GameState gameState, String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            filename = generateFilename(gameState);
        }
        
        if (!filename.endsWith(FILE_EXTENSION)) {
            filename += FILE_EXTENSION;
        }
        
        Path filePath = Paths.get(SAVE_DIR, filename);
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println("# Sudoku Game Save File");
            writer.println("player=" + (gameState.getPlayerName() != null ? gameState.getPlayerName() : "Unknown"));
            writer.println("difficulty=" + gameState.getDifficulty());
            writer.println("startTime=" + gameState.getStartTime());
            writer.println("saveTime=" + gameState.getSaveTime());
            writer.println("gameCompleted=" + gameState.isGameCompleted());
            writer.println("hintsUsed=" + gameState.getHintsUsed());
            
            writer.println("# Original Board");
            writeBoard(writer, gameState.getOriginalBoard(), "original");
            
            writer.println("# Current Board");
            writeBoard(writer, gameState.getCurrentBoard(), "current");
            
            writer.println("# Fixed Cells");
            writeFixedCells(writer, gameState.getFixedCells());
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            return false;
        }
    }
    
    public GameState loadGame(String filename) {
        if (!filename.endsWith(FILE_EXTENSION)) {
            filename += FILE_EXTENSION;
        }
        
        Path filePath = Paths.get(SAVE_DIR, filename);
        
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            GameState gameState = new GameState();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                
                if (line.startsWith("player=")) {
                    gameState.setPlayerName(line.substring(7));
                } else if (line.startsWith("difficulty=")) {
                    gameState.setDifficulty(Difficulty.valueOf(line.substring(11)));
                } else if (line.startsWith("startTime=")) {
                    gameState.setStartTime(Long.parseLong(line.substring(10)));
                } else if (line.startsWith("saveTime=")) {
                    gameState.setSaveTime(Long.parseLong(line.substring(9)));
                } else if (line.startsWith("gameCompleted=")) {
                    gameState.setGameCompleted(Boolean.parseBoolean(line.substring(14)));
                } else if (line.startsWith("hintsUsed=")) {
                    gameState.setHintsUsed(Integer.parseInt(line.substring(10)));
                } else if (line.startsWith("original:")) {
                    gameState.setOriginalBoard(readBoard(reader));
                } else if (line.startsWith("current:")) {
                    gameState.setCurrentBoard(readBoard(reader));
                } else if (line.startsWith("fixed:")) {
                    gameState.setFixedCells(readFixedCells(reader));
                }
            }
            
            return gameState;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            return null;
        }
    }
    
    public List<String> listSavedGames() {
        List<String> games = new ArrayList<>();
        try {
            Files.list(Paths.get(SAVE_DIR))
                .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                .forEach(path -> games.add(path.getFileName().toString()));
        } catch (IOException e) {
            System.err.println("Failed to list saved games: " + e.getMessage());
        }
        return games;
    }
    
    public boolean deleteGame(String filename) {
        if (!filename.endsWith(FILE_EXTENSION)) {
            filename += FILE_EXTENSION;
        }
        
        try {
            return Files.deleteIfExists(Paths.get(SAVE_DIR, filename));
        } catch (IOException e) {
            System.err.println("Failed to delete game: " + e.getMessage());
            return false;
        }
    }
    
    private void writeBoard(PrintWriter writer, int[][] board, String prefix) {
        writer.println(prefix + ":");
        for (int[] row : board) {
            for (int i = 0; i < row.length; i++) {
                writer.print(row[i]);
                if (i < row.length - 1) writer.print(",");
            }
            writer.println();
        }
    }
    
    private void writeFixedCells(PrintWriter writer, boolean[][] fixed) {
        writer.println("fixed:");
        for (boolean[] row : fixed) {
            for (int i = 0; i < row.length; i++) {
                writer.print(row[i] ? "1" : "0");
                if (i < row.length - 1) writer.print(",");
            }
            writer.println();
        }
    }
    
    private int[][] readBoard(BufferedReader reader) throws IOException {
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            String line = reader.readLine();
            String[] values = line.split(",");
            for (int j = 0; j < 9; j++) {
                board[i][j] = Integer.parseInt(values[j]);
            }
        }
        return board;
    }
    
    private boolean[][] readFixedCells(BufferedReader reader) throws IOException {
        boolean[][] fixed = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            String line = reader.readLine();
            String[] values = line.split(",");
            for (int j = 0; j < 9; j++) {
                fixed[i][j] = "1".equals(values[j]);
            }
        }
        return fixed;
    }
    
    private String generateFilename(GameState gameState) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(new Date(gameState.getSaveTime()));
        String player = gameState.getPlayerName() != null ? gameState.getPlayerName() : "Player";
        return player + "_" + gameState.getDifficulty() + "_" + timestamp;
    }
}