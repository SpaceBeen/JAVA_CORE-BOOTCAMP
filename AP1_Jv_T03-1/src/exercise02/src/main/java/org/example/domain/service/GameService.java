package org.example.domain.service;

public interface GameService {
    int[] getNextMove(int[][] field);

    boolean validateGameField(int[][] field, int[] move);

    Integer isGameOver(int[][] field);
}
