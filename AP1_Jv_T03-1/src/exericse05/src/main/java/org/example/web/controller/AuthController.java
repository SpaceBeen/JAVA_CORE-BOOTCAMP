package org.example.web.controller;

import jakarta.validation.Valid;
import org.example.domain.service.AuthService;
import org.example.web.model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody SignUpRequest request) {
        boolean success = authService.register(request);
        return success ? ResponseEntity.ok("User registered successfully")
                : ResponseEntity.badRequest().body("User already exists");
    }

    @PostMapping("/login")
    public ResponseEntity<UUID> login(@RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.authorize(authHeader);
        return userId != null ? ResponseEntity.ok(userId)
                : ResponseEntity.status(401).body(null);
    }
}