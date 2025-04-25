package org.example.domain.service;

import org.example.datasource.mapper.UserMapper;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public boolean registerUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            return false; // Пользователь с таким логином уже существует
        }
        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userMapper.toEntity(user));
        return true;
    }

    public User findByLoginAndPassword(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // Проверка пароля
                .map(userMapper::toDomain)
                .orElse(null);
    }
}