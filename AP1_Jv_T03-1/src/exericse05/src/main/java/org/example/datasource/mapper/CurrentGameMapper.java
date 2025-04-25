package org.example.datasource.mapper;

import org.example.datasource.model.CurrentGameEntity;
import org.example.datasource.model.GameFieldEntity;
import org.example.domain.model.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentGameMapper {

    private GameFieldMapper fieldMapper = new GameFieldMapper();

    // Преобразование из domain в datasource
    public CurrentGameEntity toEntity(CurrentGame game) {
        CurrentGameEntity entity = new CurrentGameEntity();
        GameFieldEntity fieldEntity = new GameFieldEntity();
        fieldEntity.setField(fieldMapper.toDataSourceFormat(game.getGameField().getField()));
        entity.setId(game.getId().toString());
        entity.setGameField(fieldEntity);
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
