package com.workingraccoon.backend.service;

import com.workingraccoon.backend.repository.CommentRepository;
import com.workingraccoon.backend.util.JwtUtil;
import com.workingraccoon.backend.util.XssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Map<String, Object>> getCommentsByPost(int postId) {
        return commentRepository.getCommentsByPost(postId);
    }

    public void createComment(String authHeader, int postId, String content) {
        String token = authHeader.substring(7);
        String phone = jwtUtil.extractPhone(token);
        int userId = commentRepository.getUserIdByPhone(phone);
        String safeContent = XssUtils.sanitize(content);
        commentRepository.createComment(postId, userId, safeContent);
    }
}