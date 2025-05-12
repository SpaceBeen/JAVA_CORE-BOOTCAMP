package org.example.domain.service;

import org.example.datasource.mapper.CurrentGameMapper;
import org.example.datasource.model.CurrentGameEntity;
import org.example.datasource.repository.CurrentGameRepository;
import org.example.domain.model.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {
    private static final int PLAYER_X = 1;
    private static final int PLAYER_O = -1;
    private static final int EMPTY = 0;
    private final CurrentGameRepository gameRepository;
    private final CurrentGameMapper mapper = new CurrentGameMapper(); // Внедряем вручную, так как нет @Autowired

    @Autowired
    public GameServiceImpl(CurrentGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public CurrentGame getGame(UUID gameId) {
        return gameRepository.findById(gameId.toString())
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public void saveGame(CurrentGame game) {
        System.out.println("Saving game with ID: " + game.getId());
        CurrentGameEntity existingEntity = gameRepository.findById(game.getId().toString()).orElse(null);
        if (existingEntity != null) {
            // Обновляем существующую запись
            CurrentGameEntity updatedEntity = mapper.toEntity(game);
            updatedEntity.setId(existingEntity.getId()); // Сохраняем существующий ID
            if (existingEntity.getGameField() != null) {
                updatedEntity.getGameField().setId(existingEntity.getGameField().getId()); // Сохраняем ID поля
            }
            gameRepository.save(updatedEntity);
        } else {
            // Создаём новую запись, если игра не существует
            gameRepository.save(mapper.toEntity(game));
        }
        System.out.println("Game saved successfully with ID: " + game.getId());
    }


    @Override
    public int[] getNextMove(int[][] field) {
        int bestValue = Integer.MAX_VALUE; // Минимизация для компьютера
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == EMPTY) {
                    field[i][j] = PLAYER_O;
                    int moveValue = minimax(field, 0, true);
                    field[i][j] = EMPTY;

                    if (moveValue < bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    @Override
    public boolean validateGameField(int[][] field, int[] move) {
        for (int[] availableMove : getAvailableMoves(field)) {
            if (availableMove[0] == move[0] && availableMove[1] == move[1]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer isGameOver(int[][] board) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i];
            }
        }
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }
        boolean isDraw = true;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == EMPTY) {
                    isDraw = false;
                    break;
                }
            }
        }
        return isDraw ? 0 : null;
    }


    @Override
    public void printField(int[][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    private List<int[]> getAvailableMoves(int[][] field) {
        List<int[]> availableMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == EMPTY) {
                    availableMoves.add(new int[]{i, j});
                }
            }
        }
        return availableMoves;
    }

    private int minimax(int[][] field, int depth, boolean isMaximizing) {
        int score = evaluate(field);
        if (score == 10) return score - depth;
        if (score == -10) return score + depth;
        if (!isMovesLeft(field)) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field[i][j] == EMPTY) {
                        field[i][j] = PLAYER_X;
                        best = Math.max(best, minimax(field, depth + 1, false));
                        field[i][j] = EMPTY;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field[i][j] == EMPTY) {
                        field[i][j] = PLAYER_O;
                        best = Math.min(best, minimax(field, depth + 1, true));
                        field[i][j] = EMPTY;
                    }
                }
            }
            return best;
        }
    }

    private int evaluate(int[][] b) {
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == PLAYER_X) return +10;
                else if (b[row][0] == PLAYER_O) return -10;
            }
        }
        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == PLAYER_X) return +10;
                else if (b[0][col] == PLAYER_O) return -10;
            }
        }
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == PLAYER_X) return +10;
            else if (b[0][0] == PLAYER_O) return -10;
        }
        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == PLAYER_X) return +10;
            else if (b[0][2] == PLAYER_O) return -10;
        }
        return 0;
    }

    private boolean isMovesLeft(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) return true;
            }
        }
        return false;
    }
}