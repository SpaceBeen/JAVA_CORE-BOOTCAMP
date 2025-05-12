package org.example.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpRequest {
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 3, max = 20, message = "Логин должен быть от 3 до 20 символов")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
    private String password;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}