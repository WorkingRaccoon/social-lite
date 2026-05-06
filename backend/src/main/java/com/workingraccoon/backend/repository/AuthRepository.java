package com.workingraccoon.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuthRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> findUserByPhone(String phone) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM Users WHERE phone_number = ?", phone
        );
        return rows.isEmpty() ? null : rows.get(0);
    }
}