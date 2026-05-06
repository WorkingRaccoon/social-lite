package com.workingraccoon.backend.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}