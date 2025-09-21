#!/bin/bash

# JavaFX Sudoku Game Compilation Script

echo "Compiling JavaFX Sudoku Game..."

# Create output directories
mkdir -p out/classes
mkdir -p out/resources

# Copy resources
cp -r src/main/resources/* out/resources/

# Compile Java files (assuming JavaFX is in module path)
javac -d out/classes \
      -cp ".:out/classes" \
      --module-path /usr/local/javafx/lib \
      --add-modules javafx.controls,javafx.fxml \
      -sourcepath src/main/java \
      src/main/java/com/sudoku/*.java \
      src/main/java/com/sudoku/*/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run the JavaFX GUI:"
    echo "java --module-path /usr/local/javafx/lib --add-modules javafx.controls,javafx.fxml -cp out/classes:out/resources com.sudoku.ui.SudokuFXApp"
    echo ""
    echo "To run the console version:"
    echo "java -cp out/classes com.sudoku.SudokuApplication"
else
    echo "Compilation failed!"
    echo "Note: Make sure JavaFX is installed and the module path is correct"
    exit 1
fi