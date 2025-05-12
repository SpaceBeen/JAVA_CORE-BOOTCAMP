package org.example.web.controller;

import org.example.domain.model.CurrentGame;
import org.example.domain.model.GameMode;
import org.example.domain.model.User;
import org.example.domain.service.GameService;
import org.example.domain.service.UserService;
import org.example.web.mapper.GameMapper;
import org.example.web.model.CreateGameRequest;
import org.example.web.model.ErrorResponseDTO;
import org.example.web.model.GameDTO;
import org.example.web.model.MoveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:63342")
public class GameController {
    private final GameService gameService;
    private final UserService userService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, UserService userService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.userService = userService;
        this.gameMapper = gameMapper;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createNewGame(@RequestHeader("Authorization") String authHeader, @RequestBody CreateGameRequest request) {
        try {
            UUID userId = extractUserId(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }
            GameMode mode = GameMode.valueOf(request.getMode().toUpperCase());
            CurrentGame newGame = gameService.createGame(userId, mode);
            return ResponseEntity.ok(gameMapper.toGameDTO(newGame));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid game mode"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableGames(@RequestHeader("Authorization") String authHeader) {
        try {
            UUID userId = extractUserId(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }
            List<CurrentGame> games = gameService.getAvailableGames();
            List<GameDTO> dtos = games.stream().map(gameMapper::toGameDTO).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<?> joinGame(@PathVariable String gameId, @RequestHeader("Authorization") String authHeader) {
        try {
            UUID gameUuid = UUID.fromString(gameId);
            UUID userId = extractUserId(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }
            CurrentGame game = gameService.joinGame(gameUuid, userId);
            return ResponseEntity.ok(gameMapper.toGameDTO(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid game ID"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> updateGame(@PathVariable String gameId, @RequestHeader("Authorization") String authHeader, @RequestBody(required = false) MoveDTO moveDTO) {
        try {
            UUID gameUuid = UUID.fromString(gameId);
            UUID userId = extractUserId(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }

            CurrentGame game;
            if (moveDTO == null) {
                // Если moveDTO отсутствует, возвращаем текущее состояние игры
                game = gameService.getGame(gameUuid);
            } else {
                // Выполняем ход
                int[] move = moveDTO.getMove();
                if (move == null || move.length != 2) {
                    return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid move coordinates"));
                }
                game = gameService.updateGame(gameUuid, userId, move[0], move[1]);
            }

            return ResponseEntity.ok(gameMapper.toGameDTO(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid game ID or move"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(@PathVariable String gameId, @RequestHeader("Authorization") String authHeader) {
        try {
            UUID gameUuid = UUID.fromString(gameId);
            UUID userId = extractUserId(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized"));
            }
            CurrentGame game = gameService.getGame(gameUuid);
            return ResponseEntity.ok(gameMapper.toGameDTO(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid game ID"));
        }
    }

    private UUID extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }
        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        User user = userService.findByLogin(parts[0]);
        if (user != null && userService.findByLoginAndPassword(parts[0], parts[1]) != null) {
            return user.getId();
        }
        return null;
    }
}