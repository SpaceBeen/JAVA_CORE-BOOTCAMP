package org.example.web.model;

import java.util.UUID;

public class GameDTO {
    private UUID id;
    private int[][] gameBoard;
    private String status;

    public GameDTO(UUID id, int[][] gameBoard) {
        this(id, gameBoard, "Player's turn");
    }

    public GameDTO(UUID id, int[][] gameBoard, String status) {
        this.id = id;
        this.gameBoard = gameBoard;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}