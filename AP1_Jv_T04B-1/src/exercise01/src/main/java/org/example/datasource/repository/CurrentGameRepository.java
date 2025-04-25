package org.example.datasource.repository;

import org.example.datasource.model.CurrentGameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrentGameRepository extends CrudRepository<CurrentGameEntity, UUID> {
    List<CurrentGameEntity> findByStateAndGameMode(String state, String gameMode);
}