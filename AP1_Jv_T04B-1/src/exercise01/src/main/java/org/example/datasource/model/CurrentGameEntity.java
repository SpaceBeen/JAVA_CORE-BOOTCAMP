package org.example.datasource.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "current_game", schema = "tictactoe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentGameEntity {
    @Id
    private UUID id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "game_field_id")
    private GameFieldEntity gameField;

    @Column(name = "state")
    private String state;

    @Column(name = "game_mode")
    private String gameMode;

    @Column(name = "x_player")
    private UUID xPlayer;

    @Column(name = "o_player")
    private UUID oPlayer;

    @Column(name = "player_id")
    private UUID playerId; // Добавляем поле
}