package com.workingraccoon.backend.service;

import com.workingraccoon.backend.repository.PostRepository;
import com.workingraccoon.backend.util.JwtUtil;
import com.workingraccoon.backend.util.XssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Map<String, Object>> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public Map<String, Object> getPostById(int postId) {
        return postRepository.getPostById(postId);
    }

    public Map<String, Object> createPost(String authHeader, String content, MultipartFile image) throws Exception {
        int userId = getUserIdFromToken(authHeader);
        String safeContent = XssUtils.sanitize(content);

        int postId = postRepository.createPost(userId, safeContent);

        if (image != null && !image.isEmpty()) {
            String uploadDir = "uploads/posts/" + postId + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, image.getBytes());

            String imagePath = "/" + uploadDir + fileName;
            postRepository.updatePostImage(postId, imagePath);
        }

        return Map.of("message", "發文成功", "postId", postId);
    }

    public void updatePost(String authHeader, int postId, String content, String image) {
        int userId = getUserIdFromToken(authHeader);
        String safeContent = XssUtils.sanitize(content);
        postRepository.updatePost(postId, userId, safeContent, image);
    }

    public boolean deletePost(String authHeader, int postId) {
        int userId = getUserIdFromToken(authHeader);
        boolean deleted = postRepository.deletePost(postId, userId);
        if (deleted) {
            postRepository.deletePostFolder(postId);
        }
        return deleted;
    }

    private int getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String phone = jwtUtil.extractPhone(token);
        return postRepository.getUserIdByPhone(phone);
    }
}