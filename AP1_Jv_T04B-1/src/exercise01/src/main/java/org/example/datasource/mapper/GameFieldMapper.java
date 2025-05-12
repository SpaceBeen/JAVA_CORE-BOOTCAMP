package org.example.datasource.mapper;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class GameFieldMapper {
    private static final Logger log = LoggerFactory.getLogger(GameFieldMapper.class);

    // Сериализация игрового поля (int[][] -> String)
    public String toDataSourceFormat(int[][] gameField) {
        if (gameField == null || gameField.length == 0) {
            log.warn("Input gameField is null or empty, returning default 3x3 field");
            gameField = new int[3][3]; // Создаём пустое поле 3x3
        }

        StringBuilder builder = new StringBuilder();
        for (int[] row : gameField) {
            if (row == null) {
                log.warn("Row in gameField is null, treating as empty row");
                row = new int[3]; // Заполняем нули, если строка null
            }
            for (int cell : row) {
                builder.append(cell).append(" ");
            }
            builder.setLength(builder.length() - 1); // Удаляем последний пробел
            builder.append("\n");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1); // Удаляем последний \n
        }

        String result = builder.toString();
        log.info("Serialized game field: {}", result);
        if (result.isEmpty()) {
            log.warn("Serialized result is empty, returning default 3x3 field");
            result = "0 0 0\n0 0 0\n0 0 0";
        }
        return result;
    }

    // Десериализация игрового поля (String -> int[][])
    public int[][] toDomainFormat(String gameFieldString) {
        if (gameFieldString == null || gameFieldString.trim().isEmpty()) {
            log.warn("Input gameFieldString is null or empty, returning default 3x3 field");
            return new int[3][3];
        }

        String[] rows = gameFieldString.split("\n");
        int[][] gameField = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].trim().split("\\s+"); // Учитываем множественные пробелы
            gameField[i] = new int[cells.length];
            for (int j = 0; j < cells.length; j++) {
                try {
                    gameField[i][j] = Integer.parseInt(cells[j]);
                } catch (NumberFormatException e) {
                    log.error("Invalid number format in cell [{}][{}]: {}", i, j, cells[j]);
                    gameField[i][j] = 0; // Заполняем нули при ошибке
                }
            }
        }
        return gameField;
    }
}