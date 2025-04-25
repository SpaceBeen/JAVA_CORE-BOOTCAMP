package org.example.domain.model;

public enum GameMode {
    HUMAN("Игра с человеком"),
    COMPUTER("Игра с компьютером");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GameMode fromDisplayName(String displayName) {
        for (GameMode mode : values()) {
            if (mode.displayName.equals(displayName)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown game mode: " + displayName);
    }
}