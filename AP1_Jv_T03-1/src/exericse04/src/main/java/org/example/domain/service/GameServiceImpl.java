package org.example.domain.service;

import org.example.datasource.reposiroty.CurrentGameRepositoryImpl;
import org.example.domain.model.CurrentGame;
import org.example.datasource.reposiroty.CurrentGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {
    private static final int PLAYER_X = 1; // Игрок
    private static final int PLAYER_O = -1; // Компьютер
    private static final int EMPTY = 0; // Пустая клетка
    private final CurrentGameRepository gameRepository;

    @Autowired
    public GameServiceImpl(CurrentGameRepositoryImpl gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public int[] getNextMove(int[][] field) {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == EMPTY) {
                    field[i][j] = PLAYER_X; // Ход игрока X
                    int moveValue = minimax(field, 0, false);
                    field[i][j] = EMPTY; // Отмена хода

                    if (moveValue > bestValue) {
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
        for (int[] i : getAvailableMoves(field)) {
            if (i[0] == move[0] && i[1] == move[1]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer isGameOver(int[][] board) {
        // Проверяем строки, столбцы и диагонали
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
        // Проверяем на ничью
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
    public CurrentGame getGame(UUID gameId) {
        return gameRepository.findById(gameId.toString());
    }

    @Override
    public void saveGame(CurrentGame game) {
        gameRepository.save(game);
    }

    @Override
    public void printField(int[][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(field[i][j]);
                System.out.print(" ");
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

        // Если игрок X выигрывает
        if (score == 10) return score - depth;
        // Если игрок O выигрывает
        if (score == -10) return score + depth;
        // Если ничья
        if (isMovesLeft(field) == false) return 0;

        // Максимизирующий игрок
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
        } else { // Минимизирующий игрок
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
    int evaluate(int[][] b) {
        // Проверка строк
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == PLAYER_X) return +10;
                else if (b[row][0] == PLAYER_O) return -10;
            }
        }

        // Проверка столбцов
        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == PLAYER_X) return +10;
                else if (b[0][col] == PLAYER_O) return -10;
            }
        }

        // Проверка диагоналей
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == PLAYER_X) return +10;
            else if (b[0][0] == PLAYER_O) return -10;
        }

        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == PLAYER_X) return +10;
            else if (b[0][2] == PLAYER_O) return -10;
        }

        return 0; // Ничья
    }

    boolean isMovesLeft(int[][] board) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == EMPTY)
                    return true;
        return false;
    }

}

/*
 static int[] findBestMove(char[][] board) {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_X; // Ход игрока X
                    int moveValue = minimax(board, 0, false);
                    board[i][j] = EMPTY; // Отмена хода

                    if (moveValue > bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    static int minimax(char[][] board, int depth, boolean isMax) {
        int score = evaluate(board);

        // Если игрок X выигрывает
        if (score == 10) return score - depth;
        // Если игрок O выигрывает
        if (score == -10) return score + depth;
        // Если ничья
        if (isMovesLeft(board) == false) return 0;

        // Максимизирующий игрок
        if (isMax) {
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER_X;
                        best = Math.max(best, minimax(board, depth + 1, false));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        } else { // Минимизирующий игрок
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER_O;
                        best = Math.min(best, minimax(board, depth + 1, true));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        }
    }

    static int evaluate(char[][] b) {
        // Проверка строк
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == PLAYER_X) return +10;
                else if (b[row][0] == PLAYER_O) return -10;
            }
        }

        // Проверка столбцов
        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == PLAYER_X) return +10;
                else if (b[0][col] == PLAYER_O) return -10;
            }
        }

        // Проверка диагоналей
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == PLAYER_X) return +10;
            else if (b[0][0] == PLAYER_O) return -10;
        }

        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == PLAYER_X) return +10;
            else if (b[0][2] == PLAYER_O) return -10;
        }

        return 0; // Ничья
    }

    static boolean isMovesLeft(char[][] board) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == EMPTY)
                    return true;
        return false;
    }
*/