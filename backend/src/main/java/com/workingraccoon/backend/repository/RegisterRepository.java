package com.workingraccoon.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RegisterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isPhoneExists(String phone) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT user_id FROM Users WHERE phone_number = ?", phone
        );
        return !rows.isEmpty();
    }

    public void createUser(String username, String phone, String email,
                           String password, String intro, String coverImagePath) {
        jdbcTemplate.update(
                "INSERT INTO Users (user_name, phone_number, email, password, biography, cover_image) VALUES (?, ?, ?, ?, ?, ?)",
                username, phone, email, password, intro, coverImagePath
        );
    }
}