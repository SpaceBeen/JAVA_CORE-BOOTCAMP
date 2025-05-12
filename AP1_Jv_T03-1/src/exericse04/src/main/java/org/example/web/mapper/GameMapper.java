package org.example.web.mapper;

import org.example.domain.model.CurrentGame;
import org.example.web.model.GameDTO;
import org.example.web.model.GameResponse;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {
    public GameDTO toGameDTO(CurrentGame game) {
        return new GameDTO(
                game.getId(),
                game.getGameField().getField()
        );
    }
}
