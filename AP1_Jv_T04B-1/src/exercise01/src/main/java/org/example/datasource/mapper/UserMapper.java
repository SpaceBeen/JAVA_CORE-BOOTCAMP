package org.example.datasource.mapper;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .rating(user.getRating()) // Исправлено
                .currentGameId(user.getCurrentGameId())
                .build();
    }

    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setLogin(entity.getLogin());
        user.setPassword(entity.getPassword());
        user.setRating(entity.getRating()); // Исправлено
        user.setCurrentGameId(entity.getCurrentGameId());
        return user;
    }
}