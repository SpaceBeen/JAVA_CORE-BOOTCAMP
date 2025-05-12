package org.example.web.controller;

import jakarta.validation.Valid;
import org.example.domain.service.AuthorisationService;
import org.example.web.model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:63342")
public class AuthorisationController {
    private final AuthorisationService authorisationService;

    @Autowired
    public AuthorisationController(AuthorisationService authorisationService) {
        this.authorisationService = authorisationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpRequest request) {
        System.out.println("Controller: Processing /auth/signup with login: " + request.getLogin());
        Map<String, String> response = new HashMap<>();
        try {
            boolean success = authorisationService.register(request);
            if (success) {
                response.put("message", "User registered successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "User registration failed: already exists or invalid data");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.out.println("Controller: Error in /auth/signup: " + e.getMessage());
            response.put("error", "Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("Controller: Processing /auth/login with authHeader: " + authHeader);
        Map<String, String> response = new HashMap<>();
        if (authHeader == null || authHeader.isEmpty()) {
            response.put("error", "Missing Authorization header");
            return ResponseEntity.status(401).body(response);
        }
        try {
            UUID userId = authorisationService.authorise(authHeader);
            if (userId != null) {
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            System.out.println("Controller: Error in /auth/login: " + e.getMessage());
            response.put("error", "Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}