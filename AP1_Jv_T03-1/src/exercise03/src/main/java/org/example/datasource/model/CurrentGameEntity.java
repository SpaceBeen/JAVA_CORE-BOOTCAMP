package org.example.datasource.model;

public class CurrentGameEntity {
    private String id; // UUID хранится как строка
    private GameFieldEntity gameField; // Матрица хранится как JSON-строка

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameFieldEntity getGameField() {
        return gameField;
    }

    public void setGameField(GameFieldEntity gameField) {
        this.gameField = gameField;
    }
}
