package com.workingraccoon.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CommentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getCommentsByPost(int postId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_comments_by_post");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_post_id", postId);
        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public void createComment(int postId, int userId, String content) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_create_comment");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_post_id", postId)
                .addValue("p_user_id", userId)
                .addValue("p_content", content);
        jdbcCall.execute(params);
    }

    public int getUserIdByPhone(String phone) {
        var rows = jdbcTemplate.queryForList(
                "SELECT user_id FROM Users WHERE phone_number = ?", phone
        );
        return (int) rows.get(0).get("user_id");
    }
}