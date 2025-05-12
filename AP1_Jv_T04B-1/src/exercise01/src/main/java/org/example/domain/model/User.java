package org.example.domain.model;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class User {
    private UUID id;
    private String login;
    private String password;
    private Integer rating; // Исправлено с raiting
    private UUID currentGameId;

    public User() {
    }

    public User(String login, String password) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.rating = 0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public UUID getCurrentGameId() { return currentGameId; }
    public void setCurrentGameId(UUID currentGameId) { this.currentGameId = currentGameId; }
}