package org.example.web.model;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String login;
    private Integer rating;
    private UUID currentGameId; // Добавляем поле

    public UserDTO() {}

    public UserDTO(UUID id, String login, Integer rating, UUID currentGameId) {
        this.id = id;
        this.login = login;
        this.rating = rating;
        this.currentGameId = currentGameId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public UUID getCurrentGameId() { return currentGameId; }
    public void setCurrentGameId(UUID currentGameId) { this.currentGameId = currentGameId; }
}