package com.example.registration.controller;

import com.example.registration.model.ApiResponse;
import com.example.registration.model.LoginRequest;
import com.example.registration.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Validated @RequestBody LoginRequest request, HttpSession session) {
        boolean ok = authService.login(request.getUsername(), request.getPassword());
        if (!ok) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("用户名或密码错误"));
        }
        session.setAttribute("LOGIN_USER", request.getUsername());
        session.setAttribute("LOGIN_ROLE", authService.getRole(request.getUsername()));

        Map<String, Object> data = new HashMap<>();
        data.put("username", request.getUsername());
        data.put("role", authService.getRole(request.getUsername()));
        data.put("sessionId", session.getId());
        return ResponseEntity.ok(ApiResponse.ok("登录成功", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("退出成功", null));
    }
}
