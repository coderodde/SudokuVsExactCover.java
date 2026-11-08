package io.github.coderodde.puzzles.sudoku;

import java.util.Arrays;

/**
 *
 */
final class IntSet {
    
    private final boolean[] set;
    
    IntSet(int cap) {
        set = new boolean[cap];
    }
    
    boolean contains(int index) {
        return set[index];
    }
    
    void add(int index) {
        set[index] = true;
    }
    
    void remove(int index) {
        set[index] = false;
    }
    
    void clear() {
        for (int i = 0; i < set.length; ++i) {
            remove(i);
        }
    }
}
