package org.example.web.model;

import java.util.UUID;

public class GameDTO {
    private UUID id;
    private int[][] gameBoard;
    private String status;
    private String mode;
    private UUID xplayer; // Используем xplayer для совместимости с фронтендом
    private UUID oplayer; // Используем oplayer
    private UUID playerId;

    public GameDTO() {}

    private GameDTO(Builder builder) {
        this.id = builder.id;
        this.gameBoard = builder.gameBoard;
        this.status = builder.status;
        this.mode = builder.mode;
        this.xplayer = builder.xplayer;
        this.oplayer = builder.oplayer;
        this.playerId = builder.playerId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private int[][] gameBoard;
        private String status;
        private String mode;
        private UUID xplayer;
        private UUID oplayer;
        private UUID playerId;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder gameBoard(int[][] gameBoard) { this.gameBoard = gameBoard; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder mode(String mode) { this.mode = mode; return this; }
        public Builder xplayer(UUID xplayer) { this.xplayer = xplayer; return this; }
        public Builder oplayer(UUID oplayer) { this.oplayer = oplayer; return this; }
        public Builder playerId(UUID playerId) { this.playerId = playerId; return this; }
        public GameDTO build() { return new GameDTO(this); }
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int[][] getGameBoard() { return gameBoard; }
    public void setGameBoard(int[][] gameBoard) { this.gameBoard = gameBoard; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public UUID getXplayer() { return xplayer; }
    public void setXplayer(UUID xplayer) { this.xplayer = xplayer; }
    public UUID getOplayer() { return oplayer; }
    public void setOplayer(UUID oplayer) { this.oplayer = oplayer; }
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
}