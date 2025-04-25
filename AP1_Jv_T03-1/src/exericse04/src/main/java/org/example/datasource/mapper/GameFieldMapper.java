package org.example.datasource.mapper;

import org.springframework.stereotype.Component;

@Component
public class GameFieldMapper {
    // Сериализация игрового поля (int[][] -> String)
    public String toDataSourceFormat(int[][] gameField) {
        StringBuilder builder = new StringBuilder();
        for (int[] row : gameField) {
            for (int cell : row) {
                builder.append(cell).append(" "); // Используем пробел как разделитель
            }
            builder.setLength(builder.length() - 1); // Удаляем последнюю запятую
            builder.append("\n"); // Добавляем разделитель строк
        }
        builder.setLength(builder.length() - 1); // Удаляем последний разделитель
        return builder.toString();
    }

    // Десериализация игрового поля (String -> int[][])
    public int[][] toDomainFormat(String gameFieldString) {
        String[] rows = gameFieldString.split("\n"); // Разделяем строки
        int[][] gameField = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].split(" "); // Разделяем значения в строке
            gameField[i] = new int[cells.length];
            for (int j = 0; j < cells.length; j++) {
                gameField[i][j] = Integer.parseInt(cells[j]);
            }
        }
        return gameField;
    }
}
