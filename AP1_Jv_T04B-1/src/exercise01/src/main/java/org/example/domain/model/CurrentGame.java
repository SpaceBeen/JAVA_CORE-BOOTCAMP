package org.example.domain.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentGame {
    private UUID id;
    private GameField gameField;
    private GameStatus state;
    private UUID playerId;
    private GameMode gameMode;
    private UUID xPlayer;
    private UUID oPlayer;
}