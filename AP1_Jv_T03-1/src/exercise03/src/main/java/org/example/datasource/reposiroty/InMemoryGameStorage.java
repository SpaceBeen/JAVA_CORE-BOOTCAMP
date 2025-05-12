package org.example.datasource.reposiroty;

import org.example.datasource.mapper.CurrentGameMapper;
import org.example.datasource.model.CurrentGameEntity;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameStorage {
    //Коллекция для хранения игр по ID
    private final ConcurrentHashMap<String, CurrentGameEntity> games = new ConcurrentHashMap<>();
    final CurrentGameMapper currentGameMapper;

    public InMemoryGameStorage(CurrentGameMapper currentGameMapper) {
        this.currentGameMapper = currentGameMapper;
    }

    CurrentGameEntity findById(String gameId) {
        return games.get(gameId);
    }

    void save(String id, CurrentGameEntity currentGameEntity) {
        games.put(currentGameEntity.getId(), currentGameEntity);
    }
/*


    // Метод для сохранения игры
    public void saveGame(CurrentGameEntity gameEntity) {
        if (gameEntity != null && gameEntity.getId() != null) {
            games.put(gameEntity.getId(), gameEntity);
        }
    }

    // Метод для получения игры по ID
    public CurrentGameEntity getGame(String gameId) {
        return games.get(gameId);
    }

    // Метод для проверки наличия игры в хранилище
    public boolean gameExists(String gameId) {
        return games.containsKey(gameId);
    }

    // Метод для удаления игры
    public void removeGame(String gameId) {
        games.remove(gameId);
    }*/


}
