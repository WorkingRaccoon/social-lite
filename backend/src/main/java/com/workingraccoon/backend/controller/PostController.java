package com.workingraccoon.backend.controller;

import com.workingraccoon.backend.DTO.PostRequest;
import com.workingraccoon.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable int postId) {
        Map<String, Object> post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.status(404).body("文章不存在");
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            return ResponseEntity.ok(postService.createPost(authHeader, content, image));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("發文失敗：" + e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int postId,
            @RequestBody PostRequest request) {
        postService.updatePost(authHeader, postId, request.getContent(), request.getImage());
        return ResponseEntity.ok(Map.of("message", "編輯成功"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int postId) {
        boolean deleted = postService.deletePost(authHeader, postId);
        if (!deleted) {
            return ResponseEntity.status(403).body("無權限刪除此文章");
        }
        return ResponseEntity.ok(Map.of("message", "刪除成功"));
    }
}