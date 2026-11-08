package io.github.coderodde.puzzles.sudoku;

import io.github.coderodde.combinatorics.exactcover.converters.Sudoku;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.BOX_DIMENSION;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.DIMENSION;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.MAXIMUM_NUMBER;
import static io.github.coderodde.combinatorics.exactcover.converters.Sudoku.MINIMUM_NUMBER;
import java.util.Objects;

/**
 *
 */
public final class SudokuVerifier {
    
    private SudokuVerifier() {}
    
    public static boolean isValidSolvedSudoku(Sudoku sudoku) {
        Objects.requireNonNull(sudoku, "The input sudoku is null.");
        
        IntSet[] row = new IntSet[DIMENSION];
        IntSet[] col = new IntSet[DIMENSION];
        IntSet[][] box = new IntSet[DIMENSION]
                                   [DIMENSION];
        
        int capacity = DIMENSION + 1;
        
        for (int i = 0; i < DIMENSION; ++i) {
            row[i] = new IntSet(capacity);
            col[i] = new IntSet(capacity);
        }
        
        for (int y = 0; y < BOX_DIMENSION; ++y) {
            for (int x = 0; x < BOX_DIMENSION; ++x) {
                box[y][x] = new IntSet(capacity);
            }
        }
        
        for (int y = 0; y < DIMENSION; ++y) {
            for (int x = 0; x < DIMENSION; ++x) {
                int number = sudoku.get(x, y);
                
                if (!isValidNumber(number)) {
                    return false;
                }
                
                if (row[y].contains(number) ||
                    col[x].contains(number)) {
                    return false;
                }
                
                int boxX = x / BOX_DIMENSION;
                int boxY = y / BOX_DIMENSION;
                
                if (box[boxY][boxX].contains(number)) {
                    return false;
                }
                
                row[y].add(number);
                col[x].add(number);
                box[boxY][boxX].add(number);
            }
        }
        
        return true;
    }
    
    private static boolean isValidNumber(int number) {
        return MINIMUM_NUMBER <= number && number <= MAXIMUM_NUMBER;
    }
}
