package org.example.web.controller;

import org.example.domain.model.User;
import org.example.domain.service.UserService;
import org.example.web.model.ErrorResponseDTO;
import org.example.web.model.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:63342")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId, @RequestHeader("Authorization") String authHeader) {
        try {
            UUID userUuid = UUID.fromString(userId);
            UUID authUserId = extractUserId(authHeader);
            if (authUserId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }
            User user = userService.findById(userUuid);
            if (user == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("User not found"));
            }
            return ResponseEntity.ok(new UserDTO(
                    user.getId(),
                    user.getLogin(),
                    user.getRating(), // Исправлено с getRaiting
                    user.getCurrentGameId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid user ID"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        UUID authUserId = extractUserId(authHeader);
        if (authUserId == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
        }
        User user = userService.findById(authUserId);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("User not found"));
        }
        return ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getLogin(),
                user.getRating(), // Исправлено с getRaiting
                user.getCurrentGameId()
        ));
    }

    private UUID extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }
        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) {
                return null;
            }
            User user = userService.findByLoginAndPassword(parts[0], parts[1]);
            return user != null ? user.getId() : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}