package com.workingraccoon.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getAllPosts() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_all_posts");
        Map<String, Object> result = jdbcCall.execute();
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> getPostById(int postId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_post_by_id");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_post_id", postId);
        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");
        return (resultSet != null && !resultSet.isEmpty()) ? resultSet.get(0) : null;
    }

    public int createPost(int userId, String content) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_create_post");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_user_id", userId)
                .addValue("p_content", content)
                .addValue("p_image", null);
        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");
        return ((Number) resultSet.get(0).get("post_id")).intValue();
    }

    public void updatePostImage(int postId, String imagePath) {
        jdbcTemplate.update("UPDATE Posts SET image = ? WHERE post_id = ?", imagePath, postId);
    }

    public void updatePost(int postId, int userId, String content, String image) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_post");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_post_id", postId)
                .addValue("p_user_id", userId)
                .addValue("p_content", content)
                .addValue("p_image", image);
        jdbcCall.execute(params);
    }

    public boolean deletePost(int postId, int userId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT image FROM Posts WHERE post_id = ? AND user_id = ?", postId, userId
        );
        if (rows.isEmpty()) return false;

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_delete_post");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_post_id", postId)
                .addValue("p_user_id", userId);
        jdbcCall.execute(params);
        return true;
    }

    public void deletePostFolder(int postId) {
        Path uploadDir = Paths.get("uploads/posts/" + postId);
        if (Files.exists(uploadDir)) {
            try {
                Files.walk(uploadDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try { Files.delete(path); }
                            catch (IOException e) { System.out.println("刪除失敗：" + path); }
                        });
            } catch (IOException e) {
                System.out.println("刪除資料夾失敗：" + e.getMessage());
            }
        }
    }

    public int getUserIdByPhone(String phone) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT user_id FROM Users WHERE phone_number = ?", phone
        );
        return (int) rows.get(0).get("user_id");
    }
}