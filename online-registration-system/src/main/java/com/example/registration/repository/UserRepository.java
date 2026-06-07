package com.example.registration.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUsernameAndPassword(String username, String password) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_user WHERE username = ? AND password = ?",
                Integer.class, username, password);
        return count != null && count > 0;
    }

    public String findRoleByUsername(String username) {
        return jdbcTemplate.queryForObject(
                "SELECT role FROM sys_user WHERE username = ?",
                String.class, username);
    }
}
