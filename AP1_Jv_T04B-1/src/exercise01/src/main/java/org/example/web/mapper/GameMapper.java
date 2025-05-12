package org.example.web.mapper;

import org.example.domain.model.CurrentGame;
import org.example.domain.model.GameMode;
import org.example.web.model.GameDTO;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {
    public GameDTO toGameDTO(CurrentGame game) {
        return GameDTO.builder()
                .id(game.getId())
                .gameBoard(game.getGameField().getField())
                .status(game.getState().getDisplayName())
                .mode(game.getGameMode() == GameMode.HUMAN ? "Игра с человеком" : "Игра с компьютером")
                .xplayer(game.getXPlayer()) // Используем xplayer
                .oplayer(game.getOPlayer()) // Используем oplayer
                .playerId(game.getPlayerId())
                .build();
    }
}