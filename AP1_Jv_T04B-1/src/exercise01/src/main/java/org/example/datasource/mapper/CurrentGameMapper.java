package org.example.datasource.mapper;

import org.example.datasource.model.CurrentGameEntity;
import org.example.datasource.model.GameFieldEntity;
import org.example.domain.model.CurrentGame;
import org.example.domain.model.GameField;
import org.example.domain.model.GameMode;
import org.example.domain.model.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CurrentGameMapper {
    private static final Logger log = LoggerFactory.getLogger(CurrentGameMapper.class);
    private final GameFieldMapper fieldMapper;

    public CurrentGameMapper(GameFieldMapper fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    public CurrentGameEntity toEntity(CurrentGame game) {
        CurrentGameEntity entity = new CurrentGameEntity();
        entity.setId(game.getId());

        GameFieldEntity fieldEntity = new GameFieldEntity();
        int[][] gameField = game.getGameField().getField();
        if (gameField == null) {
            log.warn("Game field is null for game ID: {}, creating default 3x3 field", game.getId());
            gameField = new int[3][3];
        }
        String fieldData = fieldMapper.toDataSourceFormat(gameField);
        if (fieldData == null || fieldData.trim().isEmpty()) {
            log.error("Field data is null or empty for game ID: {}", game.getId());
            throw new IllegalStateException("Поле игрового поля не может быть null или пустым");
        }
        log.info("Setting game field data for game ID {}: {}", game.getId(), fieldData);
        fieldEntity.setField(fieldData);
        log.info("GameFieldEntity field after setting: {}", fieldEntity.getField());
        entity.setGameField(fieldEntity);

        entity.setState(game.getState().getDisplayName());
        entity.setGameMode(game.getGameMode() != null ? game.getGameMode().getDisplayName() : null);
        entity.setXPlayer(game.getXPlayer());
        entity.setOPlayer(game.getOPlayer());
        entity.setPlayerId(game.getPlayerId());
        return entity;
    }

    public CurrentGame toDomain(CurrentGameEntity entity) {
        CurrentGame game = new CurrentGame();
        game.setId(entity.getId());

        // Инициализация gameField
        game.setGameField(new GameField());

        String fieldData = entity.getGameField() != null ? entity.getGameField().getField() : null;
        if (fieldData == null) {
            log.warn("Stored game field is null for game ID: {}, using default 3x3 field", entity.getId());
            fieldData = "0 0 0\n0 0 0\n0 0 0";
        }
        game.getGameField().setField(fieldMapper.toDomainFormat(fieldData));

        game.setState(GameStatus.fromDisplayName(entity.getState()));
        game.setPlayerId(entity.getXPlayer());
        game.setGameMode(entity.getGameMode() != null ? GameMode.fromDisplayName(entity.getGameMode()) : null);
        game.setXPlayer(entity.getXPlayer());
        game.setOPlayer(entity.getOPlayer());
        game.setPlayerId(entity.getPlayerId());
        return game;
    }
}