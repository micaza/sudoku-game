package com.sudoku.ui;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.animation.ScaleTransition;
import javafx.animation.FillTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SudokuCell extends TextField {
    private final int row;
    private final int col;
    private boolean isFixed;
    private SudokuFXController controller;
    
    public SudokuCell(int row, int col, SudokuFXController controller) {
        this.row = row;
        this.col = col;
        this.controller = controller;
        this.isFixed = false;
        
        setupCell();
        setupEventHandlers();
    }
    
    private void setupCell() {
        setPrefSize(50, 50);
        setMaxSize(50, 50);
        setMinSize(50, 50);
        
        getStyleClass().add("sudoku-cell");
        setStyle("-fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold;");
    }
    
    private void setupEventHandlers() {
        textProperty().addListener((obs, oldText, newText) -> {
            if (isFixed) {
                setText(oldText);
                return;
            }
            
            if (newText.length() > 1) {
                setText(oldText);
                return;
            }
            
            if (newText.isEmpty()) {
                controller.makeMove(row, col, 0);
            } else if (newText.matches("[1-9]")) {
                int value = Integer.parseInt(newText);
                if (!controller.makeMove(row, col, value)) {
                    setText(oldText);
                    animateError();
                } else {
                    animateSuccess();
                }
            } else {
                setText(oldText);
            }
        });
        
        focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (newFocus) {
                controller.highlightRelated(row, col);
            } else {
                controller.clearHighlights();
            }
        });
    }
    
    public void setValue(int value, boolean fixed) {
        this.isFixed = fixed;
        
        if (value == 0) {
            setText("");
        } else {
            setText(String.valueOf(value));
        }
        
        updateStyle();
    }
    
    private void updateStyle() {
        getStyleClass().removeAll("fixed-cell", "user-cell", "error-cell", "highlight-cell");
        
        if (isFixed) {
            getStyleClass().add("fixed-cell");
        } else {
            getStyleClass().add("user-cell");
        }
    }
    
    public void setHighlight(boolean highlight) {
        if (highlight) {
            getStyleClass().add("highlight-cell");
        } else {
            getStyleClass().remove("highlight-cell");
        }
    }
    
    public void setError(boolean error) {
        if (error) {
            getStyleClass().add("error-cell");
        } else {
            getStyleClass().remove("error-cell");
        }
    }
    
    private void animateSuccess() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), this);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
    
    private void animateError() {
        ScaleTransition shake = new ScaleTransition(Duration.millis(100), this);
        shake.setFromX(1.0);
        shake.setToX(0.9);
        shake.setAutoReverse(true);
        shake.setCycleCount(4);
        shake.play();
    }
    
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isFixed() { return isFixed; }
}