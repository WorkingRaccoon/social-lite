package com.workingraccoon.backend.service;

import com.workingraccoon.backend.repository.AuthRepository;
import com.workingraccoon.backend.service.TokenBlacklistService;
import com.workingraccoon.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public Map<String, Object> login(String phone, String password) {
        Map<String, Object> user = authRepository.findUserByPhone(phone);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, (String) user.get("password"))) {
            return null;
        }

        String token = jwtUtil.generateToken(phone);

        return Map.of(
                "token", token,
                "tokenType", "Bearer",
                "expiresIn", 1800,
                "userId", user.get("user_id"),
                "userName", user.get("user_name")
        );
    }

    public void logout(String authHeader) {
        String token = authHeader.substring(7);
        Date expiration = jwtUtil.extractExpiration(token);
        tokenBlacklistService.addToBlacklist(token, expiration);
    }
}