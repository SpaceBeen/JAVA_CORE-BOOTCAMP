package org.example.domain.model;

public enum GameStatus {
    WAITING_FOR_PLAYERS("Ожидание игроков"),
    PLAYER_TURN("Ход игрока"),
    PLAYER_WON("Победа игрока"),
    COMPUTER_WON("Победа компьютера"),
    DRAW("Ничья");

    private final String displayName;

    GameStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GameStatus fromDisplayName(String displayName) {
        for (GameStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown game status: " + displayName);
    }
}