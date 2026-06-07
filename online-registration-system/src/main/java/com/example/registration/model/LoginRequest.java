package com.example.registration.model;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "username不能为空")
    private String username;

    @NotBlank(message = "password不能为空")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.trim(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? null : password.trim(); }
}
