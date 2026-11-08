package io.github.coderodde.puzzles.sudoku;

import io.github.coderodde.combinatorics.exactcover.converters.Sudoku;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.BOX_DIMENSION;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.DIMENSION;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.MAXIMUM_NUMBER;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.MINIMUM_NUMBER;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.UNUSED_NUMBER;
import java.util.Objects;

/**
 *
 */
public final class SudokuSolver {
    
    private final IntSet[] row = new IntSet[DIMENSION];
    private final IntSet[] col = new IntSet[DIMENSION];
    private final IntSet[][] box = new IntSet[BOX_DIMENSION]
                                             [BOX_DIMENSION];
    
    private Sudoku solution;
    private Sudoku instance;
    
    public SudokuSolver() {
        final int intSetCapacity = DIMENSION + 1;
        
        for (int r = 0; r < row.length; ++r) {
            row[r] = new IntSet(intSetCapacity);
        }
        
        for (int c = 0; c < col.length; ++c) {
            col[c] = new IntSet(intSetCapacity);
        }
        
        for (int y = 0; y < box.length; ++y) {
            for (int x = 0; x < box[y].length; ++x) {
                box[y][x] = new IntSet(intSetCapacity);
            }
        }
    }
    
    public Sudoku solve(Sudoku instance) {
        this.instance = 
            Objects.requireNonNull(
                instance, 
                "The input sudoku instance is null.");
        
        clearState();
        checkAndPreload();
        solution = new Sudoku();
        return solve(0, 0) ? solution : null;
    }
    
    private boolean solve(int x, int y) {
        if (x == DIMENSION) {
            x = 0;
            ++y;
        }
        
        if (y == DIMENSION) {
            return true;
        }
        
        if (instance.get(x, y) != UNUSED_NUMBER) {
            solution.set(x, y, instance.get(x, y));
            return solve(x + 1, y);
        }
        
        for (int number = MINIMUM_NUMBER;
                 number <= MAXIMUM_NUMBER; 
                 number++) {
            
            if (row[y].contains(number)) {
                continue;
            }
            
            if (col[x].contains(number)) {
                continue;
            }
            
            int boxX = x / BOX_DIMENSION;
            int boxY = y / BOX_DIMENSION;
            
            if (box[boxY][boxX].contains(number)) {
                continue;
            }
            
            row[y].add(number);
            col[x].add(number);
            box[boxY][boxX].add(number);
            
            solution.set(x, y, number);
            
            if (solve(x + 1, y)) {
                return true;
            }
            
            row[y].remove(number);
            col[x].remove(number);
            box[boxY][boxX].remove(number);
            solution.set(x, y, UNUSED_NUMBER);
        }
        
        return false;
    }
    
    private void clearState() {
        for (IntSet is : row) {
            is.clear();
        }
        
        for (IntSet is : col) {
            is.clear();
        }
        
        for (IntSet[] iss : box) {
            for (IntSet is : iss) {
                is.clear();
            }
        }
    }
    
    private void checkAndPreload() {
        for (int y = 0; y < DIMENSION; ++y) {
            for (int x = 0; x < DIMENSION; ++x) {
                int number = instance.get(x, y);
                
                if (number != UNUSED_NUMBER) {
                    if (row[y].contains(number) || col[x].contains(number)) {
                        throw new IllegalArgumentException();
                    }
                    
                    int boxY = y / BOX_DIMENSION;
                    int boxX = x / BOX_DIMENSION;
                    
                    if (box[boxY][boxX].contains(number)) {
                        throw new IllegalArgumentException();
                    }
                    
                    row[y].add(number);
                    col[x].add(number);
                    box[boxY][boxX].add(number);
                }
            }
        }
    }
}
