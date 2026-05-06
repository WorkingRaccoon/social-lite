package com.workingraccoon.backend.controller;

import com.workingraccoon.backend.DTO.LoginRequest;
import com.workingraccoon.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request.getPhone(), request.getPassword());
        if (result == null) {
            return ResponseEntity.status(401).body("手機號碼或密碼錯誤");
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/token")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }
}