package org.example.domain.service;

import org.example.domain.model.GameField;

import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl implements GameService {
    private static final int PLAYER_X = 1; // Игрок
    private static final int PLAYER_O = -1; // Компьютер
    private static final int EMPTY = 0; // Пустая клетка

    @Override
    public int[] getNextMove(int[][] field) {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int[] move : getAvailableMoves(field)) {
            field[move[0]][move[1]] = PLAYER_O; // Сделать ход O
            int moveValue = minimax(field, 0, false);
            field[move[0]][move[1]] = EMPTY;

            if (moveValue > bestValue) {
                bestMove[0] = move[0];
                bestMove[1] = move[1];
                bestValue = moveValue;
            }
        }
        return bestMove;
    }

    @Override
    public boolean validateGameField(int[][] field,int[] move) {
        for (int[] i: getAvailableMoves(field) ) {
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


    private int minimax(int[][] newField, int depth, boolean isMaximizing) {
        Integer winner = isGameOver(newField);
        if (winner == PLAYER_X) return -10 + depth; // X проигрывает
        if (winner == PLAYER_O) return 10 - depth; // O выигрывает
        if (winner == EMPTY) return 0; // Ничья

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int[] move : getAvailableMoves(newField)) {
                newField[move[0]][move[1]] = PLAYER_O;
                int score = minimax(newField, depth + 1, false);
                newField[move[0]][move[1]] = EMPTY;
                bestScore = Math.max(score, bestScore);
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int[] move : getAvailableMoves(newField)) {
                newField[move[0]][move[1]] = PLAYER_X;
                int score = minimax(newField, depth + 1, true);
                newField[move[0]][move[1]] = EMPTY;
                bestScore = Math.min(score, bestScore);
            }
            return bestScore;
        }
    }
}

/*
//Логика запуска и игры
    public void playGame() {
        while (true) {
            printBoard();
            if (currentPlayer == PLAYER_X) { // Ход игрока X
                System.out.println("Player X's turn. Enter row and column:");
                // Здесь можно добавить ввод от пользователя
                // Например: int row = scanner.nextInt(); int col = scanner.nextInt();
                // Для простоты примера можно использовать фиксированные значения:
                int row = 1, col = 1; // Замените на ввод пользователя
                if (board[row][col] == EMPTY) {
                    board[row][col] = PLAYER_X;
                    currentPlayer = PLAYER_O;
                } else {
                    System.out.println("Invalid move! Try again.");
                    continue;
                }
            } else { // Ход игрока O
                System.out.println("Player O's turn.");
                int[] move = findBestMove();
                if (move[0] != -1) {
                    board[move[0]][move[1]] = PLAYER_O;
                    currentPlayer = PLAYER_X;
                    System.out.println("Player O moved to: " + move[0] + ", " + move[1]);
                }
            }

            char winner = checkWinner();
            if (winner != EMPTY) {
                printBoard();
                System.out.println("Winner: " + winner);
                break;
            } else if (isBoardFull()) {
                printBoard();
                System.out.println("It's a draw!");
                break;
            }
        }
    }
*/