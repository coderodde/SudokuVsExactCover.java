package io.github.coderodde.puzzles.sudoku.benchmark;

import io.github.coderodde.combinatorics.exactcover.AlgorithmXExactCoverSolver;
import io.github.coderodde.combinatorics.exactcover.ExactCoverInstance;
import io.github.coderodde.combinatorics.exactcover.ExactCoverSolution;
import io.github.coderodde.combinatorics.exactcover.ExactCoverSolver;
import io.github.coderodde.combinatorics.exactcover.converters.SudokuExactCoverDecoder;
import io.github.coderodde.combinatorics.exactcover.converters.Sudoku;
import io.github.coderodde.combinatorics.exactcover.converters.SudokuExactCoverEncoder;
import io.github.coderodde.puzzles.sudoku.SudokuSolver;
import io.github.coderodde.statistics.run.RunStatistics;
import io.github.coderodde.statistics.run.Runner;
import io.github.coderodde.statistics.run.SignificanceLevel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public final class Benchmark {
    
    private static final int ITERATIONS = 100;
    private static final double SIGNIFICANCE_LEVEL_VALUE = 0.05;
    private static final SignificanceLevel SIGNIFICANCE_LEVEL = 
                     new SignificanceLevel(SIGNIFICANCE_LEVEL_VALUE);
    
    private static final int[][] ARTO_INKALA_SUDOKU_INT_GRID = {
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
    
    private static final Sudoku ARTO_INKALA_SUDOKU = 
                     new Sudoku(ARTO_INKALA_SUDOKU_INT_GRID);
    
    private static class AbstractRunnable implements Runnable {
        
        protected final List<Sudoku> results = new ArrayList<>();
        
        public List<Sudoku> getResults() {
            return results;
        }

        @Override
        public void run() {
        
        }
    }
    
    private static final class SudokuRunnable extends AbstractRunnable {
        
        private final SudokuSolver solver = new SudokuSolver();
        
        @Override
        public void run() {
            results.add(solver.solve(ARTO_INKALA_SUDOKU));
        }
    }
    
    private static final class ExactCoverRunnable extends AbstractRunnable {
        
        private final ExactCoverSolver solver = 
            new AlgorithmXExactCoverSolver();
        
        private final ExactCoverInstance instance = 
            new SudokuExactCoverEncoder().encode(ARTO_INKALA_SUDOKU);
        
        private final SudokuExactCoverDecoder decoder = 
            new SudokuExactCoverDecoder();
        
        @Override
        public void run() {
            ExactCoverSolution solution = solver.solve(instance);
            Sudoku sudoku = decoder.decode(solution);
            results.add(sudoku);
        }
    }
    
    private Benchmark() {}
    
    public static void main(String[] args) {
        AbstractRunnable sudokuRunnable     = new SudokuRunnable();
        AbstractRunnable exactCoverRunnable = new ExactCoverRunnable();
        
        RunStatistics sudokuRunStatistics = 
            Runner.measure(sudokuRunnable, 
                           ITERATIONS, 
                           SIGNIFICANCE_LEVEL);
        
        RunStatistics exactCoverRunStatistics = 
            Runner.measure(exactCoverRunnable, 
                           ITERATIONS, 
                           SIGNIFICANCE_LEVEL);
        
        System.out.println("=== Sudoku solver ===");
        System.out.println(sudokuRunStatistics);
        
        System.out.println();
        
        System.out.println("=== Exact cover solver (Algorithm X) ===");
        System.out.println(exactCoverRunStatistics);
        
        System.out.println("");
        
        System.out.printf(
            "> Algorithms agree: %b.%n", 
            sudokuRunnable.getResults()
                          .equals(exactCoverRunnable.getResults()));    
    }
}
