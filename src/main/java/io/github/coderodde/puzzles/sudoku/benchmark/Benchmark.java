package io.github.coderodde.puzzles.sudoku.benchmark;

import io.github.coderodde.combinatorics.exactcover.AlgorithmXExactCoverSolver;
import io.github.coderodde.combinatorics.exactcover.ExactCoverInstance;
import io.github.coderodde.combinatorics.exactcover.ExactCoverSolution;
import io.github.coderodde.combinatorics.exactcover.converters.ExactCoverInstanceToSudokuConverter;
import io.github.coderodde.combinatorics.exactcover.converters.Sudoku;
import io.github.coderodde.combinatorics.exactcover.converters.SudokuToExactCoverInstanceConverter;
import io.github.coderodde.puzzles.sudoku.SudokuSolver;
import static io.github.coderodde.puzzles.sudoku.SudokuVerifier.isValidSolvedSudoku;

/**
 * 
 */
public final class Benchmark {
    
    private Benchmark() {}
    
    public static void main(String[] args) {
        int[][] grid = {
            { 8, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 3, 6, 0, 0, 0, 0, 0 },
            { 0, 7, 0, 0, 9, 0, 2, 0, 0 },
            { 0, 5, 0, 0, 0, 7, 0, 0, 0 },
            { 0, 0, 0, 0, 4, 5, 7, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 3, 0 },
            { 0, 0, 1, 0, 0, 0, 0, 6, 8 },
            { 0, 0, 8, 5, 0, 0, 0, 1, 0 },
            { 0, 9, 0, 0, 0, 0, 4, 0, 0 },
        };
        
        Sudoku sudoku             = new Sudoku(grid); // An empty sudoku.
        SudokuSolver sudokuSolver = new SudokuSolver();
        AlgorithmXExactCoverSolver exactCoverSolver = 
            new AlgorithmXExactCoverSolver();
        
        for (int i = 0; i < 100; ++i) {
            long t        = System.nanoTime();
            Sudoku solved = sudokuSolver.solve(sudoku);
            long elapsed  = System.nanoTime() - t;

            System.out.printf(
                "%d ns, Valid: %b.%n",
                elapsed,
                isValidSolvedSudoku(solved));

            ExactCoverInstance instance =
                new SudokuToExactCoverInstanceConverter()
                    .convert(sudoku);

            t = System.nanoTime();

            ExactCoverSolution exactCoverSolution =
                exactCoverSolver.solve(instance);

            elapsed = System.nanoTime() - t;

    //        Sudoku decoded = new ExactCoverInstanceToSudokuConverter().convert(instance)

            System.out.println(elapsed);
        }
    }
}
