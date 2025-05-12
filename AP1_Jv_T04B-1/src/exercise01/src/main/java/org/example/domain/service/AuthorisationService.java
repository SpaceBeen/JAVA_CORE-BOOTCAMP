package org.example.domain.service;

import org.example.domain.model.User;
import org.example.web.model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class AuthorisationService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthorisationService(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(SignUpRequest request) {
        System.out.println("Регистрация: логин = [" + request.getLogin() + "], пароль = [" + request.getPassword() + "]");
        if (request.getLogin() == null || request.getPassword() == null) {
            System.out.println("Ошибка: логин или пароль равны null");
            return false;
        }
        if (request.getLogin().length() < 3 || request.getPassword().length() < 6) {
            System.out.println("Ошибка: логин или пароль слишком короткие");
            return false;
        }
        boolean success = userService.registerUser(new User(request.getLogin(), request.getPassword())); // Передаём нехешированный пароль
        System.out.println("Результат регистрации: " + success);
        return success;
    }

    public UUID authorise(String authHeader) {
        System.out.println("Получен заголовок: [" + authHeader + "]");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            System.out.println("Ошибка: неверный формат заголовка");
            return null;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        System.out.println("Base64 строка: " + base64Credentials);
        String credentials;
        try {
            credentials = new String(Base64.getDecoder().decode(base64Credentials));
            System.out.println("Декодированные данные: " + credentials);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка декодирования Base64: " + e.getMessage());
            return null;
        }

        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            System.out.println("Ошибка: неверный формат логина и пароля");
            return null;
        }

        String login = parts[0];
        String password = parts[1];
        User user = userService.findByLogin(login);
        if (user == null) {
            System.out.println("Пользователь с логином " + login + " не найден");
            return null;
        }

        System.out.println("Хэш пароля в базе: " + user.getPassword());
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Пароль совпадает: " + passwordMatch);
        if (passwordMatch) {
            System.out.println("Авторизация успешна, UUID: " + user.getId());
            return user.getId();
        } else {
            System.out.println("Неверный пароль");
            return null;
        }
    }
}