package org.example.datasource.mapper;

import org.example.datasource.model.CurrentGameEntity;
import org.example.domain.model.CurrentGame;

import java.util.UUID;

public class CurrentGameMapper {
    GameFieldMapper fieldMapper;

    // Преобразование из domain в datasource
    public CurrentGameEntity toEntity(CurrentGame game) {
        CurrentGameEntity entity = new CurrentGameEntity();
        entity.setId(game.getId().toString());
        entity.getGameField().setField(fieldMapper.toDataSourceFormat(game.getGameField().getField()));
        return entity;
    }

    // Преобразование из datasource в domain
    public CurrentGame toDomain(CurrentGameEntity entity) {
        CurrentGame game = new CurrentGame();
        game.setId(UUID.fromString(entity.getId()));
        game.setField(fieldMapper.toDomainFormat(entity.getGameField().getField()));
        return game;
    }


}
