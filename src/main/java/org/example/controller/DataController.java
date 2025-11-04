package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.CreatePostRequest;
import org.example.dto.PostResponse;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DataController {

    private final PostService postService;
    private final UserRepository userRepository;

    @Autowired
    public DataController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/data - получение списка всех постов
     * Доступен только аутентифицированным пользователям
     */
    @GetMapping("/data")
    public ResponseEntity<List<PostResponse>> getData() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * POST /api/posts - создание нового поста
     * Третий метод (придуманный самостоятельно)
     * Доступен только аутентифицированным пользователям
     */
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse post = postService.createPost(request);
        return ResponseEntity.ok(post);
    }

    /**
     * GET /api/posts/my - получение постов текущего пользователя
     * Дополнительный метод для удобства
     */
    @GetMapping("/posts/my")
    public ResponseEntity<List<PostResponse>> getMyPosts() {
        List<PostResponse> posts = postService.getMyPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * GET /api/users - получение списка всех пользователей
     * Дополнительный метод для демонстрации
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/me - получение информации о текущем пользователе
     * Дополнительный метод для удобства
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}

