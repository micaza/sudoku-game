package com.sudoku.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SudokuFXApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudoku.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/sudoku.css").toExternalForm());
        
        primaryStage.setTitle("Sudoku Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}