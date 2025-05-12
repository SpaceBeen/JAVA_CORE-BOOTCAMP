package org.example.web.controller;

import org.example.domain.model.CurrentGame;
import org.example.domain.service.GameService;
import org.example.web.mapper.GameMapper;
import org.example.web.model.ErrorResponseDTO;
import org.example.web.model.GameDTO;
import org.example.web.model.MoveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:63342") // Разрешаем запросы с порта IntelliJ
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @PostMapping("/new")
    public ResponseEntity<GameDTO> createNewGame() {
        CurrentGame newGame = new CurrentGame();
        gameService.saveGame(newGame);
        GameDTO response = gameMapper.toGameDTO(newGame);
        System.out.println("New game created with ID: " + newGame.getId()); // Для отладки
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> updateGame(@PathVariable UUID gameId, @RequestBody(required = false) MoveDTO moveDTO) {
        try {
            CurrentGame currentGame = gameService.getGame(gameId);
            if (currentGame == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Game not found"));
            }

            if (moveDTO == null) {
                GameDTO response = gameMapper.toGameDTO(currentGame);
                return ResponseEntity.ok(response);
            }

            if (!gameService.validateGameField(currentGame.getGameField().getField(), moveDTO.getMove())) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid move"));
            }

            // Ход игрока
            currentGame.getGameField().setField(moveDTO.getMove()[0], moveDTO.getMove()[1], 1);
            Integer gameOver = gameService.isGameOver(currentGame.getGameField().getField());
            if (gameOver != null) {
                String winner = gameOver == 1 ? "Player wins" : gameOver == -1 ? "Computer wins" : "Draw";
                gameService.saveGame(currentGame); // Обновляем существующую игру
                return ResponseEntity.ok(new GameDTO(currentGame.getId(), currentGame.getGameField().getField(), winner));
            }

            // Ход компьютера
            int[] computerMove = gameService.getNextMove(currentGame.getGameField().getField());
            currentGame.getGameField().setField(computerMove[0], computerMove[1], -1);
            gameService.saveGame(currentGame); // Обновляем существующую игру

            gameOver = gameService.isGameOver(currentGame.getGameField().getField());
            if (gameOver != null) {
                String winner = gameOver == 1 ? "Player wins" : gameOver == -1 ? "Computer wins" : "Draw";
                return ResponseEntity.ok(new GameDTO(currentGame.getId(), currentGame.getGameField().getField(), winner));
            }

            GameDTO response = gameMapper.toGameDTO(currentGame);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }
}