package org.example.datasource.reposiroty;

import org.example.datasource.mapper.CurrentGameMapper;
import org.example.datasource.model.CurrentGameEntity;
import org.example.domain.model.CurrentGame;

import java.util.UUID;

public class CurrentGameRepositoryImpl implements CurrentGameRepository {

    private final InMemoryGameStorage games;
    private final CurrentGameMapper mapper;

    public CurrentGameRepositoryImpl(InMemoryGameStorage games, CurrentGameMapper mapper) {
        this.games = games;
        this.mapper = mapper;
    }

    @Override
    public void save(CurrentGame game) {
        games.save(mapper.toEntity(game).getId(), mapper.toEntity(game));
    }

    @Override
    public CurrentGame findById(String id) {
        return mapper.toDomain(games.findById(id));
    }
}