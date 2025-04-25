package org.example.datasource.model;

import jakarta.persistence.*;

@Entity
@Table(name = "current_game", schema = "tictactoe")
public class CurrentGameEntity {
    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_field_id")
    private GameFieldEntity gameField;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public GameFieldEntity getGameField() { return gameField; }
    public void setGameField(GameFieldEntity gameField) { this.gameField = gameField; }
}