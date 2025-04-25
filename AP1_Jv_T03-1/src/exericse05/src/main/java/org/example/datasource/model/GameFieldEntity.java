package org.example.datasource.model;

import jakarta.persistence.*;

@Entity
@Table(name = "game_field", schema = "tictactoe")
public class GameFieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field")
    private String gameField;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getField() { return gameField; }
    public void setField(String gameField) { this.gameField = gameField; }
}