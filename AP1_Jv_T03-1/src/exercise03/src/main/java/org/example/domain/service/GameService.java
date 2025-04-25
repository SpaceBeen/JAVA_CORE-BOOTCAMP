package org.example.domain.service;

import org.example.domain.model.CurrentGame;

import java.util.UUID;

public interface GameService {
    int[] getNextMove(int[][] field);

    boolean validateGameField(int[][] field, int[] move);

    Integer isGameOver(int[][] field);

    CurrentGame getGame(UUID gameId);

    void saveGame(CurrentGame game);
}
