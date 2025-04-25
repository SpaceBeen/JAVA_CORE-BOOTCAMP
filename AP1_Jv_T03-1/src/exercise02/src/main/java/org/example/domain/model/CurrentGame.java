package org.example.domain.model;

import java.util.UUID;

public class CurrentGame {
    private UUID id;
    private GameField field;

    public CurrentGame(GameField field){
        this.id = UUID.randomUUID();
        this.field = field;
    }
    public UUID getGameId() {
        return id;
    }
    public GameField getGameField() {
        return field;
    }
}
