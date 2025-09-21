#!/bin/bash

# Sudoku Game Compilation Script

echo "Compiling Sudoku Game..."

# Create output directory
mkdir -p out

# Compile all Java files
javac -d out -sourcepath src/main/java src/main/java/com/sudoku/*.java src/main/java/com/sudoku/*/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the game: java -cp out com.sudoku.SudokuApplication"
else
    echo "Compilation failed!"
    exit 1
fi