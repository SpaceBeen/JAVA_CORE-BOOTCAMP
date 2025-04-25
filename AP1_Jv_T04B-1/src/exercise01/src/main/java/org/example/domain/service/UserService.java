package org.example.domain.service;

import org.example.datasource.mapper.UserMapper;
import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            return false;
        }
        UserEntity entity = userMapper.toEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword())); // Хешируем пароль
        userRepository.save(entity);
        return true;
    }

    public User findByLoginAndPassword(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(entity -> passwordEncoder.matches(password, entity.getPassword())) // Проверяем хеш
                .map(userMapper::toDomain)
                .orElse(null);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toDomain)
                .orElse(null);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain)
                .orElse(null);
    }
}