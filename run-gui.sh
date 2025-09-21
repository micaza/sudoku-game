#!/bin/bash

# Simple script to run the JavaFX GUI version
# This assumes JavaFX is available in the system

echo "Starting Sudoku JavaFX GUI..."

# Try different common JavaFX locations
JAVAFX_PATHS=(
    "/usr/local/javafx/lib"
    "/opt/javafx/lib"
    "$HOME/javafx/lib"
    "/usr/share/openjfx/lib"
)

JAVAFX_PATH=""
for path in "${JAVAFX_PATHS[@]}"; do
    if [ -d "$path" ]; then
        JAVAFX_PATH="$path"
        break
    fi
done

if [ -z "$JAVAFX_PATH" ]; then
    echo "JavaFX not found. Please install JavaFX and update the path in this script."
    echo "You can download JavaFX from: https://openjfx.io/"
    echo ""
    echo "Alternative: Run without module path (may work with some JDK distributions):"
    echo "java -cp out/classes:out/resources com.sudoku.ui.SudokuFXApp"
    exit 1
fi

echo "Using JavaFX from: $JAVAFX_PATH"

java --module-path "$JAVAFX_PATH" \
     --add-modules javafx.controls,javafx.fxml \
     -cp out/classes:out/resources \
     com.sudoku.ui.SudokuFXApp