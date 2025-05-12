package org.example.datasource.reposiroty;

import org.example.datasource.model.CurrentGameEntity;
import org.example.domain.model.CurrentGame;

public interface CurrentGameRepository {
    CurrentGame findById(String gameId);

    void save(CurrentGame game);
}
