package org.example.web.model;

public class MoveDTO {
    private int[] move; // [row, col]

    public MoveDTO() {}

    public MoveDTO(int[] move) {
        this.move = move;
    }

    public int[] getMove() {
        return move;
    }

    public void setMove(int[] move) {
        this.move = move;
    }
}
