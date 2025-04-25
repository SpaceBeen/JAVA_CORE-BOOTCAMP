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
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> updateGame(@PathVariable UUID gameId, @RequestBody MoveDTO moveDTO) {
        try {
            CurrentGame currentGame = gameService.getGame(gameId);
            gameService.printField(currentGame.getGameField().getField());

            if (!gameService.validateGameField(currentGame.getGameField().getField(), moveDTO.getMove())) {
                return ResponseEntity.badRequest().body("invalid move");
            }

            if (gameService.isGameOver(currentGame.getGameField().getField()) != null)
                return ResponseEntity.ok("game Over,winner is " + gameService.isGameOver(currentGame.getGameField().getField()).toString());

            currentGame.getGameField().setField(moveDTO.getMove()[0], moveDTO.getMove()[1], 1);

            gameService.saveGame(currentGame);

            int[] next_move = gameService.getNextMove(currentGame.getGameField().getField());

            currentGame.getGameField().setField(next_move[0], next_move[1], -1);

            gameService.printField(currentGame.getGameField().getField());

            if (gameService.isGameOver(currentGame.getGameField().getField()) != null)
                return ResponseEntity.ok("game Over,winner is " + gameService.isGameOver(currentGame.getGameField().getField()).toString());

            gameService.saveGame(currentGame);

            GameDTO response = gameMapper.toGameDTO(currentGame);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        }
    }

}
