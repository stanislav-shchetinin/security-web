package org.example.config;

import org.example.entity.Post;
import org.example.entity.User;
import org.example.repository.PostRepository;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Initializing test data...");

            // Создание тестовых пользователей
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullName("Admin User");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);

            User user1 = new User();
            user1.setUsername("john");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setEmail("john@example.com");
            user1.setFullName("John Doe");
            user1.setRole(User.Role.USER);
            user1.setIsActive(true);

            User user2 = new User();
            user2.setUsername("jane");
            user2.setPassword(passwordEncoder.encode("password123"));
            user2.setEmail("jane@example.com");
            user2.setFullName("Jane Smith");
            user2.setRole(User.Role.USER);
            user2.setIsActive(true);

            userRepository.save(admin);
            userRepository.save(user1);
            userRepository.save(user2);

            // Создание тестовых постов
            Post post1 = new Post();
            post1.setTitle("Welcome to Security Web API");
            post1.setContent("This is the first post in our new API. We're using Spring Boot with JWT authentication.");
            post1.setAuthor(admin);

            Post post2 = new Post();
            post2.setTitle("Getting Started with Spring Security");
            post2.setContent("Spring Security is a powerful framework for authentication and authorization.");
            post2.setAuthor(user1);

            Post post3 = new Post();
            post3.setTitle("Understanding JWT Tokens");
            post3.setContent("JSON Web Tokens are a compact way to securely transmit information between parties.");
            post3.setAuthor(user2);

            Post post4 = new Post();
            post4.setTitle("PostgreSQL with Docker");
            post4.setContent("Using Docker to run PostgreSQL makes database management much easier.");
            post4.setAuthor(user1);

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

