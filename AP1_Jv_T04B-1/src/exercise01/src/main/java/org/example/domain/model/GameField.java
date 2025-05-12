package org.example.domain.model;

import org.springframework.stereotype.Component;

@Component
public class GameField {
    private int[][] field;

    public GameField() {
        this.field = new int[3][3];
    }

    public int[][] getField() {
        return this.field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    public void setField(int row, int col, int value) {
        this.field[row][col] = value;
    }


}
