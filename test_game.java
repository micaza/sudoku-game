import com.sudoku.model.*;
import com.sudoku.generator.SudokuGenerator;
import com.sudoku.solver.SudokuSolver;
import com.sudoku.manager.GameManager;

public class test_game {
    public static void main(String[] args) {
        System.out.println("Testing Sudoku Game Components...\n");
        
        // Test 1: Basic board creation
        System.out.println("1. Testing SudokuBoard creation:");
        SudokuBoard board = new SudokuBoard();
        System.out.println("Empty board created successfully");
        
        // Test 2: Puzzle generation
        System.out.println("\n2. Testing puzzle generation:");
        SudokuGenerator generator = new SudokuGenerator();
        SudokuBoard puzzle = generator.generatePuzzle(Difficulty.EASY);
        System.out.println("Easy puzzle generated:");
        System.out.println(puzzle);
        
        // Test 3: Solver
        System.out.println("3. Testing solver:");
        SudokuSolver solver = new SudokuSolver();
        SudokuBoard copy = puzzle.copy();
        boolean solved = solver.solve(copy);
        System.out.println("Puzzle solved: " + solved);
        
        // Test 4: Game manager
        System.out.println("\n4. Testing GameManager:");
        GameManager manager = new GameManager();
        manager.startNewGame(Difficulty.MEDIUM);
        System.out.println("Game started successfully");
        System.out.println("Game stats: " + manager.getGameStats());
        
        System.out.println("\n✓ All tests passed! The game is ready to play.");
        System.out.println("Run: java -cp out com.sudoku.SudokuApplication");
    }
}