package org.example.datasource.repository;

import org.example.datasource.model.CurrentGameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentGameRepository extends CrudRepository<CurrentGameEntity, String> {
    // Здесь можно добавить кастомные методы, если нужно, например:
    // Optional<CurrentGameEntity> findById(String id); // Уже есть в CrudRepository
    // void save(CurrentGameEntity entity); // Уже есть в CrudRepository
}