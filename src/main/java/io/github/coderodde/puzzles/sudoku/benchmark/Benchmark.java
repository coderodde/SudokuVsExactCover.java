package io.github.coderodde.puzzles.sudoku.benchmark;

import io.github.coderodde.combinatorics.exactcover.AlgorithmXExactCoverSolver;
import io.github.coderodde.combinatorics.exactcover.ExactCoverInstance;
import io.github.coderodde.combinatorics.exactcover.ExactCoverSolution;
import io.github.coderodde.combinatorics.exactcover.ExactCoverSolver;
import io.github.coderodde.combinatorics.exactcover.converters.Sudoku;
import io.github.coderodde.combinatorics.exactcover.converters.SudokuExactCoverDecoder;
import io.github.coderodde.combinatorics.exactcover.converters.SudokuExactCoverEncoder;
import io.github.coderodde.puzzles.sudoku.SudokuSolver;
import io.github.coderodde.statistics.run.BenchmarkSummary;
import io.github.coderodde.statistics.run.BenchmarkRunner;
import io.github.coderodde.statistics.run.SignificanceLevel;

/**
 * This class runs a benchmark between dedicated sudoku solver and the Algorithm
 * X by Donald Knuth.
 */
public final class Benchmark {
    
    private static final int WARMUP_ITERATIONS = 1_000;
    private static final int MAJOR_ITERATIONS  = 20;
    private static final int MINOR_ITERATIONS  = 500;
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
        
        protected Sudoku result;
        
        public Sudoku getResult() {
            return result;
        }

        @Override
        public void run() {}
    }
    
    private static final class SudokuRunnable extends AbstractRunnable {
        
        private final SudokuSolver solver = new SudokuSolver();
        
        @Override
        public void run() {
            result = solver.solve(ARTO_INKALA_SUDOKU);
        }
    }
    
    private static final class ExactCoverRunnable extends AbstractRunnable {
        
        private final ExactCoverSolver solver = 
            new AlgorithmXExactCoverSolver();
        
        private final SudokuExactCoverDecoder decoder = 
            new SudokuExactCoverDecoder();
        
        @Override
        public void run() {
            ExactCoverInstance instance = new SudokuExactCoverEncoder()
                                            .encode(ARTO_INKALA_SUDOKU);
            
            ExactCoverSolution solution = solver.solve(instance);
            result = decoder.decode(solution);
        }
    }
    
    private Benchmark() {}
    
    public static void main(String[] args) {
        AbstractRunnable sudokuRunnable     = new SudokuRunnable();
        AbstractRunnable algorithmXRunnable = new ExactCoverRunnable();
        
        BenchmarkRunner sudokuBenchmarkRunner = 
            new BenchmarkRunner(SIGNIFICANCE_LEVEL);
        
        BenchmarkRunner algorithmXBenchmarkRunner = 
            new BenchmarkRunner(SIGNIFICANCE_LEVEL);
        
        // Warmup:
        sudokuBenchmarkRunner.measure(sudokuRunnable, WARMUP_ITERATIONS);
        algorithmXBenchmarkRunner.measure(
            algorithmXRunnable,
            WARMUP_ITERATIONS);
        
        for (int i = 0; i < MAJOR_ITERATIONS; ++i) {
            sudokuBenchmarkRunner.measure(sudokuRunnable, MINOR_ITERATIONS);
            
            algorithmXBenchmarkRunner.measure(algorithmXRunnable, 
                                              MINOR_ITERATIONS);
        }
        
        BenchmarkSummary sudokuSummary =
            sudokuBenchmarkRunner.getBenchmarkSummary();
        
        BenchmarkSummary algorithmXSummary = 
            algorithmXBenchmarkRunner.getBenchmarkSummary();
        
        System.out.println("=== Sudoku solver ===");
        System.out.println(sudokuSummary);
        
        System.out.println();
        
        System.out.println("=== Exact cover solver (Algorithm X) ===");
        System.out.println(algorithmXSummary);
        
        System.out.println("");
        
        System.out.printf(
            "> Algorithms agree: %b.%n", 
            sudokuRunnable.getResult()
                          .equals(algorithmXRunnable.getResult()));    
    }
}
