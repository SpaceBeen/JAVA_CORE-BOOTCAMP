package org.example.datasource.repository;

import org.example.datasource.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLogin(String login);
    Optional<UserEntity> findByLoginAndPassword(String login, String password);
    Optional<UserEntity> findByCurrentGameId(UUID gameId);
}