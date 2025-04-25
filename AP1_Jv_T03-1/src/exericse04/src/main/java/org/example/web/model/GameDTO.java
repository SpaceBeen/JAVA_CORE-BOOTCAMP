package org.example.web.model;

import java.util.UUID;

public class GameDTO {
    private UUID id;
    private int[][] gameBoard;

    public GameDTO(UUID id, int[][] gameBoard) {
        this.id = id;
        this.gameBoard = gameBoard;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

}
