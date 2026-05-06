package com.workingraccoon.backend.controller;

import com.workingraccoon.backend.DTO.CommentRequest;
import com.workingraccoon.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getCommentsByPost(@PathVariable int postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int postId,
            @RequestBody CommentRequest request) {
        try {
            commentService.createComment(authHeader, postId, request.getContent());
            return ResponseEntity.ok(Map.of("message", "留言成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("留言失敗：" + e.getMessage());
        }
    }
}