package org.example.datasource.reposiroty;

import org.example.datasource.mapper.CurrentGameMapper;
import org.example.datasource.model.CurrentGameEntity;
import org.example.domain.model.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class CurrentGameRepositoryImpl implements CurrentGameRepository {

    private final InMemoryGameStorage games;
    private final CurrentGameMapper mapper;

    @Autowired
    public CurrentGameRepositoryImpl(InMemoryGameStorage games) {
        this.games = games;
        this.mapper = new CurrentGameMapper();
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