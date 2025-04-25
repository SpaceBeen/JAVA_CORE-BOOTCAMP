package org.example.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class CurrentGame {
    private UUID id;
    private GameField field;

    public CurrentGame() {
        this.id = UUID.randomUUID();
        this.field = new GameField();
    }

    public GameField getGameField() {
        return field;
    }

    public void setField(GameField field) {
        this.field = field;
    }

    public void setField(int[][] field) {
        this.field.setField(field);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
