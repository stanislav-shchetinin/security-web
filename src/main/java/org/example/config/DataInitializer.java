package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.repository.PostRepository;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Initializing test data...");

            // Создание тестовых пользователей
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .fullName("Admin User")
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .build();

            User user1 = User.builder()
                    .username("john")
                    .password(passwordEncoder.encode("password123"))
                    .email("john@example.com")
                    .fullName("John Doe")
                    .role(User.Role.USER)
                    .isActive(true)
                    .build();

            User user2 = User.builder()
                    .username("jane")
                    .password(passwordEncoder.encode("password123"))
                    .email("jane@example.com")
                    .fullName("Jane Smith")
                    .role(User.Role.USER)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
            userRepository.save(user1);
            userRepository.save(user2);

            // Создание тестовых постов
            Post post1 = Post.builder()
                    .title("Welcome to Security Web API")
                    .content("This is the first post in our new API. We're using Spring Boot with JWT authentication.")
                    .author(admin)
                    .build();

            Post post2 = Post.builder()
                    .title("Getting Started with Spring Security")
                    .content("Spring Security is a powerful framework for authentication and authorization.")
                    .author(user1)
                    .build();

            Post post3 = Post.builder()
                    .title("Understanding JWT Tokens")
                    .content("JSON Web Tokens are a compact way to securely transmit information between parties.")
                    .author(user2)
                    .build();

            Post post4 = Post.builder()
                    .title("PostgreSQL with Docker")
                    .content("Using Docker to run PostgreSQL makes database management much easier.")
                    .author(user1)
                    .build();

            postRepository.save(post1);
            postRepository.save(post2);
            postRepository.save(post3);
            postRepository.save(post4);

            log.info("Test data initialized successfully!");
            log.info("Test users created:");
            log.info("  - admin:admin123 (ADMIN role)");
            log.info("  - john:password123 (USER role)");
            log.info("  - jane:password123 (USER role)");
        }
    }
}

