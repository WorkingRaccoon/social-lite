package com.workingraccoon.backend.controller;

import com.workingraccoon.backend.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("username") String username,
            @RequestPart("phone") String phone,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart("password") String password,
            @RequestPart(value = "intro", required = false) String intro,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        try {
            registerService.register(username, phone, email, password, intro, coverImage);
            return ResponseEntity.ok(Map.of("message", "註冊成功"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}