package org.example.domain.service;

import jakarta.transaction.Transactional;
import org.example.datasource.mapper.GameFieldMapper;
import org.example.datasource.model.CurrentGameEntity;
import org.example.datasource.model.GameFieldEntity;
import org.example.datasource.model.UserEntity;
import org.example.datasource.mapper.CurrentGameMapper;
import org.example.datasource.repository.GameFieldRepository;
import org.example.domain.model.CurrentGame;
import org.example.domain.model.GameField;
import org.example.domain.model.GameMode;
import org.example.domain.model.GameStatus;
import org.example.datasource.repository.CurrentGameRepository;
import org.example.datasource.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private static final int PLAYER_X = 1;
    private static final int PLAYER_O = -1;
    private static final int EMPTY = 0;
    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    private final CurrentGameRepository gameRepository;
    private final GameFieldRepository gameFieldRepository;
    private final UserRepository userRepository;
    private final CurrentGameMapper gameMapper;
    private final GameFieldMapper gameFieldMapper;
    private final CurrentGameRepository currentGameRepository;
    private final CurrentGameMapper currentGameMapper;

    public GameServiceImpl(CurrentGameRepository gameRepository, GameFieldRepository gameFieldRepository,
                           UserRepository userRepository, CurrentGameMapper gameMapper, GameFieldMapper gameFieldMapper, GameFieldMapper gameFieldMapper1, CurrentGameRepository currentGameRepository, CurrentGameMapper currentGameMapper) {
        this.gameRepository = gameRepository;
        this.gameFieldRepository = gameFieldRepository;
        this.userRepository = userRepository;
        this.gameMapper = gameMapper;
        this.gameFieldMapper = gameFieldMapper1;
        this.currentGameRepository = currentGameRepository;
        this.currentGameMapper = currentGameMapper;
    }

    @Transactional
    @Override
    public CurrentGame createGame(UUID userId, GameMode mode) {
        log.info("Creating game for userId: {}, mode: {}", userId, mode);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
//        if (user.getCurrentGameId() != null) {
//            log.warn("User {} is already in game {}", userId, user.getCurrentGameId());
//            throw new IllegalStateException("Пользователь уже участвует в игре");
//        }

        CurrentGame game = CurrentGame.builder()
                .id(UUID.randomUUID())
                .gameField(new GameField())
                .state(mode == GameMode.HUMAN ? GameStatus.WAITING_FOR_PLAYERS : GameStatus.PLAYER_TURN)
                .playerId(userId)
                .gameMode(mode)
                .xPlayer(userId)
                .oPlayer(mode == GameMode.COMPUTER ? null : null)
                .build();

        CurrentGameEntity entity = gameMapper.toEntity(game);
        if (entity.getGameField() == null || entity.getGameField().getField() == null) {
            log.error("GameField or field is null before saving for game ID: {}", entity.getId());
            throw new IllegalStateException("GameField или field не могут быть null");
        }

        // Явное сохранение GameFieldEntity
        GameFieldEntity gameFieldEntity = entity.getGameField();
        log.info("Saving GameFieldEntity with field: {}", gameFieldEntity.getField());
        gameFieldEntity = gameFieldRepository.save(gameFieldEntity);
        entity.setGameField(gameFieldEntity);

        log.info("Saving CurrentGameEntity with ID: {}, gameField: {}",
                entity.getId(), entity.getGameField().getField());
        gameRepository.save(entity);
        log.info("Saved game entity with id: {}", entity.getId());

        user.setCurrentGameId(game.getId());
        userRepository.save(user);
        log.info("Updated user {} with currentGameId: {}", userId, game.getId());

        return game;
    }

    @Override
    public List<CurrentGame> getAvailableGames() {
        log.info("Fetching available games");
        List<CurrentGameEntity> games = gameRepository.findByStateAndGameMode(
                GameStatus.WAITING_FOR_PLAYERS.getDisplayName(),
                GameMode.HUMAN.getDisplayName()
        );
        log.info("Found {} available games", games.size());
        return games.stream()
                .map(gameMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public CurrentGame joinGame(UUID gameId, UUID userId) {
        log.info("User {} attempting to join game {}", userId, gameId);

        // Загружаем пользователя
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));


        // Загружаем игру
        CurrentGameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        // Проверяем наличие GameFieldEntity
        if (gameEntity.getGameField() == null || gameEntity.getGameField().getField() == null) {
            log.error("GameField or field is null for game ID: {}", gameId);
            throw new IllegalStateException("Игровое поле не инициализировано");
        }

        // Преобразуем в доменную модель
        CurrentGame game = gameMapper.toDomain(gameEntity);

        // Проверяем, является ли пользователь уже участником игры (xPlayer или oPlayer)
        boolean isXPlayer = userId.equals(game.getXPlayer());
        boolean isOPlayer = userId.equals(game.getOPlayer());
        if (isXPlayer || isOPlayer) {
            log.info("User {} is already in game {} as {} player, allowing rejoin", userId, gameId, isXPlayer ? "X" : "O");
            // Обновляем currentGameId пользователя, если он ещё не установлен
            if (user.getCurrentGameId() == null) {
                user.setCurrentGameId(gameId);
                userRepository.save(user);
                log.info("Updated user {} with currentGameId: {}", userId, gameId);
            }
            return game;
        }

        // Проверяем состояние игры
        if (game.getState() == GameStatus.WAITING_FOR_PLAYERS) {
            // Проверяем, можно ли назначить пользователя как oPlayer
            if (game.getOPlayer() == null) {
                // Назначаем пользователя как oPlayer
                game.setOPlayer(userId);
                game.setState(GameStatus.PLAYER_TURN);
                game.setPlayerId(game.getXPlayer()); // Ходит X

                // Обновляем сущность игры
                gameEntity.setOPlayer(userId);
                gameEntity.setState(GameStatus.PLAYER_TURN.getDisplayName());
                gameRepository.save(gameEntity);
                log.info("Updated game {} with O_player: {}", gameId, userId);

                // Обновляем пользователя
                user.setCurrentGameId(gameId);
                userRepository.save(user);
                log.info("Updated user {} with currentGameId: {}", userId, gameId);

                return game;
            } else {
                log.warn("Game {} already has an O_player: {}", gameId, game.getOPlayer());
                throw new IllegalStateException("Место второго игрока уже занято");
            }
        } else if (game.getState() == GameStatus.PLAYER_TURN) {
            // Игра уже идёт, пользователь не является ни xPlayer, ни oPlayer
            log.warn("Game {} is in progress, user {} is not a participant", gameId, userId);
            throw new IllegalStateException("Игра уже идёт, и вы не являетесь участником");
        } else {
            // Игра завершена или в другом состоянии
            log.warn("Game {} is in state {}, cannot join", gameId, game.getState());
            throw new IllegalStateException("Игра завершена или недоступна для присоединения");
        }
    }

    @Override
    @Transactional
    public CurrentGame updateGame(UUID gameId, UUID userId, int x, int y) {
        log.info("User {} making move in game {} at position ({}, {})", userId, gameId, x, y);
        CurrentGameEntity gameEntity = currentGameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));
        CurrentGame game = gameMapper.toDomain(gameEntity);
        log.info("Current playerId before move: {}", game.getPlayerId());

        // Проверка, участвует ли пользователь в игре
        if (!userId.equals(game.getXPlayer()) && !userId.equals(game.getOPlayer())) {
            log.warn("User {} is not a participant in game {}", userId, gameId);
            throw new IllegalStateException("Пользователь не участвует в этой игре");
        }
        // Проверка состояния игры
        if (game.getState() != GameStatus.PLAYER_TURN) {
            log.warn("Game {} is not in PLAYER_TURN state, status: {}", gameId, game.getState());
            throw new IllegalStateException("Игра не в состоянии хода");
        }
        //Проверка очереди хода (для режима HUMAN)
        if (game.getGameMode() == GameMode.HUMAN && !userId.equals(game.getPlayerId())) {
            log.warn("Not user {}'s turn in game {}, expected player: {}", userId, gameId, game.getPlayerId());
            throw new IllegalStateException("Не ваш ход");
        }

        // Ход игрока
        int[][] board = game.getGameField().getField();
        if (board[x][y] != 0) {
            log.warn("Cell ({}, {}) in game {} is already occupied", x, y, gameId);
            throw new IllegalArgumentException("Клетка уже занята");
        }
        int symbol = userId.equals(game.getXPlayer()) ? 1 : -1;
        board[x][y] = symbol;
        game.getGameField().setField(board);
        log.info("Set cell ({}, {}) to {} for user {} in game {}", x, y, symbol, userId, gameId);

        // Проверка результата после хода игрока
        int result = checkGameResult(board);
        if (result == 0) {
            game.setState(GameStatus.DRAW);
            gameEntity.setState(GameStatus.DRAW.getDisplayName());
            log.info("Game {} ended in a draw", gameId);
        } else if (result == 1 || result == -1) {
            game.setState(GameStatus.PLAYER_WON);
            game.setPlayerId(userId);
            gameEntity.setState(GameStatus.PLAYER_WON.getDisplayName());
            gameEntity.setPlayerId(userId);
            log.info("User {} won game {}", userId, gameId);
        } else if (result == -1 && game.getGameMode() == GameMode.COMPUTER) {
            game.setState(GameStatus.COMPUTER_WON);
            gameEntity.setState(GameStatus.COMPUTER_WON.getDisplayName());
            gameEntity.setPlayerId(null);
            log.info("Computer won game {}", gameId);
        } else {
            // Игра продолжается
            if (game.getGameMode() == GameMode.HUMAN) {
                // В режиме HUMAN переключаем ход на другого игрока
                UUID nextPlayerId = userId.equals(game.getXPlayer()) ? game.getOPlayer() : game.getXPlayer();
                if (nextPlayerId == null) {
                    log.error("Next player ID is null for game {}", gameId);
                    throw new IllegalStateException("Не удалось определить следующего игрока");
                }
                game.setPlayerId(nextPlayerId);
                gameEntity.setPlayerId(nextPlayerId);
                log.info("Next turn for player {} in game {}", nextPlayerId, gameId);
            } else {
                // В режиме COMPUTER выполняем ход компьютера
                log.info("Performing computer move in game {}", gameId);
                makeComputerMove(board);
                game.getGameField().setField(board);
                log.info("Computer made move in game {}", gameId);

                // Проверка результата после хода компьютера
                result = checkGameResult(board);
                if (result == 0) {
                    game.setState(GameStatus.DRAW);
                    gameEntity.setState(GameStatus.DRAW.getDisplayName());
                    log.info("Game {} ended in a draw after computer move", gameId);
                } else if (result == -1) {
                    game.setState(GameStatus.COMPUTER_WON);
                    gameEntity.setState(GameStatus.COMPUTER_WON.getDisplayName());
                    gameEntity.setPlayerId(null);
                    log.info("Computer won game {} after its move", gameId);
                } else {
                    game.setPlayerId(userId);
                    gameEntity.setPlayerId(userId);
                    log.info("Next turn for player {} in game {} after computer move", userId, gameId);
                }
            }
        }

        // Сохраняем обновлённое поле
        GameFieldEntity gameFieldEntity = gameEntity.getGameField();
        gameFieldEntity.setField(gameFieldMapper.toDataSourceFormat(board));
        gameFieldRepository.save(gameFieldEntity);

        // Дополнительное логирование перед сохранением
        log.info("Saving game entity with playerId: {}", gameEntity.getPlayerId());
        currentGameRepository.save(gameEntity);

        // Проверка сохранённого состояния
        CurrentGameEntity savedEntity = currentGameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Игра не найдена после сохранения"));
        log.info("Verified saved game entity with playerId: {}", savedEntity.getPlayerId());

        // Очищаем currentGameId игроков, если игра завершена
        if (game.getState() != GameStatus.PLAYER_TURN) {
            clearUserGame(game.getXPlayer());
            clearUserGame(game.getOPlayer());
            log.info("Cleared currentGameId for players in game {}", gameId);
        }

        return game;
    }

    @Override
    public CurrentGame getGame(UUID gameId) {
        log.info("Fetching game {}", gameId);
        CurrentGameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));
        return gameMapper.toDomain(gameEntity);
    }

    private int checkGameResult(int[][] board) {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }
        // Проверка столбцов
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }
        // Проверка диагоналей
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }
        // Проверка на ничью
        boolean full = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
        }
        return full ? 0 : 2; // 2 означает игра продолжается
    }

    private void makeComputerMove(int[][] board) {
        log.info("Computer making move");
        int bestValue = Integer.MAX_VALUE; // Минимизация для компьютера
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_O;
                    int moveValue = minimax(board, 0, true);
                    board[i][j] = EMPTY;

                    if (moveValue < bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }
        board[bestMove[0]][bestMove[1]] = -1;
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

    private boolean isMovesLeft(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) return true;
            }
        }
        return false;
    }


    private void clearUserGame(UUID userId) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                user.setCurrentGameId(null);
                userRepository.save(user);
                log.info("Cleared currentGameId for user {}", userId);
            });
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
}