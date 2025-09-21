#!/bin/bash

# Swing Sudoku Game Compilation Script

echo "Compiling Swing Sudoku Game..."

# Create output directory
mkdir -p out

# Compile all Java files (Swing is included in standard JDK)
javac -d out -sourcepath src/main/java src/main/java/com/sudoku/*.java src/main/java/com/sudoku/*/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run the Swing GUI:"
    echo "java -cp out com.sudoku.ui.SudokuSwingGUI"
    echo ""
    echo "To run the console version:"
    echo "java -cp out com.sudoku.SudokuApplication"
else
    echo "Compilation failed!"
    exit 1
fi