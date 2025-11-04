package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreatePostRequest;
import org.example.dto.PostResponse;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.repository.PostRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userService.findByUsername(username);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .build();

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userService.findByUsername(username);

        return postRepository.findByAuthorId(author.getId())
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

