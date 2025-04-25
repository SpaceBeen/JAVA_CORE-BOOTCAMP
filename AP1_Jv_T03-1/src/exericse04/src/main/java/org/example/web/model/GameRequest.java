package org.example.web.model;

import java.util.UUID;

public class GameRequest {
    private UUID gameId;
    private FieldDTO field;

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public FieldDTO getField() {
        return field;
    }

    public void setField(FieldDTO field) {
        this.field = field;
    }
}
