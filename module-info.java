module sudoku.game {
    requires javafx.controls;
    requires javafx.fxml;
    
    exports com.sudoku.ui;
    exports com.sudoku.model;
    exports com.sudoku.manager;
    exports com.sudoku.solver;
    exports com.sudoku.generator;
}