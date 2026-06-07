package com.example.registration.service;

import com.example.registration.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        return userRepository.existsByUsernameAndPassword(username, password);
    }

    public String getRole(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
