package com.example.registration.controller;

import com.example.registration.model.ApiResponse;
import com.example.registration.model.RegisterRequest;
import com.example.registration.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private final RegistrationService registrationService;

    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Validated @RequestBody RegisterRequest request) {
        ApiResponse<Map<String, Object>> result = registrationService.register(request.getActivityId(), request.getStudentName());
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> count(@RequestParam Long id) {
        return ResponseEntity.ok(ApiResponse.ok("查询成功", registrationService.count(id)));
    }
}
