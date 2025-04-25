package org.example.di;

import org.example.datasource.reposiroty.CurrentGameRepository;
import org.example.domain.model.CurrentGame;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdemoApplication.class, args);

    }
    @Bean
    public CommandLineRunner run(CurrentGameRepository games) {
        return args -> {
            // Создание новой игры
            CurrentGame game = new CurrentGame();

            // Сохранение игры в репозитории
            games.save(game);

            // Вывод ID новой игры
            System.out.println("New game ID: " + game.getId());
        };
    }
}
