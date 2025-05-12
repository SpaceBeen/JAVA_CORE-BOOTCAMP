package org.example.domain.service;

import org.example.domain.model.User;
import org.example.web.model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {
    private final UserService userService;

    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public boolean register(SignUpRequest request) {
        User user = new User(request.getLogin(), request.getPassword());
        return userService.registerUser(user);
    }

    public UUID authorize(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            System.out.println("Invalid or missing Authorization header");
            return null;
        }
        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            System.out.println("Invalid credentials format");
            return null;
        }
        String login = parts[0];
        String password = parts[1];
        User user = userService.findByLoginAndPassword(login, password);
        System.out.println("Login: " + login + ", Found user: " + (user != null ? user.getId() : "null"));
        return user != null ? user.getId() : null;
    }
}