# Security Web API

REST API с аутентификацией на основе JWT, разработанное с использованием Spring Boot, Spring Security и PostgreSQL.

## Технологии

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Maven
- Docker & Docker Compose

## Функциональность

API предоставляет следующие эндпоинты:

### Аутентификация (публичные эндпоинты)

1. **POST /auth/login** - Вход в систему
   - Принимает username и password
   - Возвращает JWT токен

2. **POST /auth/register** - Регистрация нового пользователя
   - Принимает username, password, email, fullName
   - Возвращает JWT токен

### Защищенные эндпоинты (требуется аутентификация)

3. **GET /api/data** - Получение списка всех постов
   - Доступен только аутентифицированным пользователям

4. **POST /api/posts** - Создание нового поста
   - Принимает title и content
   - Автоматически привязывается к текущему пользователю

5. **GET /api/posts/my** - Получение постов текущего пользователя

6. **GET /api/users** - Получение списка всех пользователей

7. **GET /api/me** - Получение информации о текущем пользователе

## Установка и запуск

### Предварительные требования

- JDK 17 или выше
- Maven 3.6+
- Docker и Docker Compose

### Шаг 1: Запуск PostgreSQL

```bash
docker-compose up -d
```

Это запустит PostgreSQL на порту 5432 с следующими параметрами:
- База данных: `security_db`
- Пользователь: `admin`
- Пароль: `admin123`

### Шаг 2: Сборка приложения

```bash
mvn clean install
```

### Шаг 3: Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

## Тестовые данные

При первом запуске автоматически создается тестовый пользователь:

| Username    | Password    | Role  |
|-------------|-------------|-------|
| postgres    | admin123    | ADMIN |

Также создаются несколько тестовых постов.

## Примеры использования API

### 1. Вход в систему

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Ответ:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### 2. Регистрация нового пользователя

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "fullName": "New User"
  }'
```

### 3. Получение списка постов (требуется токен)

```bash
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Создание нового поста (требуется токен)

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "My New Post",
    "content": "This is the content of my new post."
  }'
```

### 5. Получение информации о текущем пользователе

```bash
curl -X GET http://localhost:8080/api/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Получение списка пользователей

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Безопасность

### Защита от SQL-инъекций (SQLi)

Приложение полностью защищено от SQL-инъекций благодаря использованию современных практик работы с базой данных:

#### 1. **ORM (Hibernate через Spring Data JPA)**
Вместо написания SQL-запросов вручную используется Hibernate ORM, который автоматически генерирует безопасные параметризованные запросы:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

#### 2. **Query Methods (автоматическая параметризация)**
Spring Data JPA автоматически преобразует названия методов в безопасные SQL-запросы с параметрами:

```java
// Метод репозитория
findByUsername("admin' OR '1'='1")

// Генерируется безопасный запрос
SELECT * FROM users WHERE username = ?
// Параметр: "admin' OR '1'='1" (обрабатывается как строковый литерал, а не SQL-код)
```

#### 3. **Отсутствие конкатенации строк**
В коде полностью отсутствует опасная конкатенация строк для формирования SQL-запросов:

#### 4. **Параметризованные запросы на всех уровнях**
Все операции с базой данных используют параметризованные запросы:
- **Поиск:** `findByUsername()`, `findByAuthorId()`
- **Проверка существования:** `existsByUsername()`, `existsByEmail()`
- **Сохранение:** `save()`, `saveAll()`
- **Обновление и удаление:** через JPA методы

Благодаря этому подходу, любые данные, вводимые пользователем (включая попытки SQL-инъекций), обрабатываются как данные, а не как исполняемый SQL-код.

### Защита от XSS (Cross-Site Scripting)

Приложение использует многоуровневую защиту от XSS-атак, характерную для REST API архитектуры:

#### 1. **Автоматическое экранирование в JSON (Jackson)**
Spring Boot использует Jackson для сериализации объектов в JSON. Jackson автоматически экранирует специальные символы при формировании JSON-ответов:

```java
// Если пользователь вводит опасный контент:
POST /api/posts
{
  "title": "<script>alert('XSS')</script>",
  "content": "<img src=x onerror='alert(1)'>"
}

// Jackson автоматически безопасно сериализует в JSON:
{
  "title": "<script>alert('XSS')</script>",
  "content": "<img src=x onerror='alert(1)'>"
}
// В JSON-строке символы < > " автоматически экранируются как Unicode (\u003C, \u003E)
```

#### 2. **Валидация входных данных (Bean Validation)**
Все входящие данные проходят валидацию на уровне DTO с использованием Jakarta Bean Validation:

```java
public class CreatePostRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
}

// В контроллере
@PostMapping("/posts")
public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
    // Данные уже провалидированы
}
```

Валидация включает:
- **Проверка формата:** `@Email`, `@NotBlank`, `@NotNull`
- **Ограничение длины:** `@Size(min=3, max=50)`
- **Обязательные поля:** `@NotBlank` предотвращает пустые значения

#### 3. **REST API архитектура**
Приложение - это REST API, возвращающее данные в формате JSON, а не рендерящее HTML на сервере. Это означает:
- Сервер не генерирует HTML с пользовательскими данными
- Ответственность за безопасное отображение данных лежит на клиентском приложении
- Снижен риск серверного XSS

#### 4. **Content-Type: application/json**
Все ответы API возвращаются с заголовком `Content-Type: application/json`, что предотвращает интерпретацию ответа как HTML браузером.

#### 5. **Рекомендации для клиентской части**
Для полной защиты от XSS клиентское приложение должно:
- Использовать современные фреймворки с автоматическим экранированием (React, Vue, Angular)
- Избегать использования `innerHTML` без санитизации
- Использовать библиотеки для санитизации HTML при необходимости отображения форматированного текста

**Текущая реализация обеспечивает достаточную защиту для REST API**, где данные передаются в JSON, а безопасное отображение обеспечивает клиентское приложение.

### Защита от Broken Authentication

Приложение реализует полноценную защиту от атак на аутентификацию согласно рекомендациям OWASP:

#### 1. **Хеширование паролей с использованием BCrypt**

Пароли не хранятся в открытом виде. Используется алгоритм BCrypt с автоматической генерацией соли:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// При регистрации пользователя
User user = new User();
user.setPassword(passwordEncoder.encode(request.getPassword())); // Хеширование пароля
userRepository.save(user);
```

**Преимущества BCrypt:**
- Автоматическая генерация соли для каждого пароля
- Защита от rainbow table атак
- Защита от brute-force атак (медленное вычисление хеша)

**Пример хранения в БД:**
```sql
-- Вместо открытого пароля "admin123" хранится:
password: $2a$10$Xk2Y.../... (60 символов BCrypt хеш)
```

#### 2. **Выдача JWT-токена при успешной аутентификации**

После успешного входа сервер генерирует и выдает JWT-токен:

```java
public LoginResponse login(LoginRequest request) {
    // 1. Аутентификация пользователя через AuthenticationManager
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword() // BCrypt автоматически сравнивает хеши
        )
    );
    
    // 2. Получение данных пользователя
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    // 3. Генерация JWT-токена
    String jwtToken = jwtService.generateToken(user);
    
    // 4. Возврат токена клиенту
    return new LoginResponse(jwtToken, user.getUsername(), user.getEmail(), user.getRole().name());
}
```

**Структура JWT-токена:**
```java
// Генерация токена с подписью HMAC-SHA256
Jwts.builder()
    .setSubject(userDetails.getUsername())        // Username в payload
    .setIssuedAt(new Date(System.currentTimeMillis()))  // Время выдачи
    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Истекает через 24 часа
    .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Подпись секретным ключом
    .compact();
```

#### 3. **Middleware для проверки JWT-токена на защищенных эндпоинтах**

Реализован фильтр `JwtAuthenticationFilter`, который проверяет токен на каждом запросе к защищенным эндпоинтам:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Извлечение токена из заголовка Authorization
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7); // Убираем "Bearer "
        
        // 2. Извлечение username из токена
        final String username = jwtService.extractUsername(jwt);
        
        // 3. Валидация токена
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            // 4. Проверка подписи и срока действия токена
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 5. Установка аутентификации в SecurityContext
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Проверка валидности токена включает:**
```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}
```
- Проверка подписи (токен не изменен)
- Проверка срока действия (не истек)
- Проверка соответствия username

#### 4. **Конфигурация защиты эндпоинтов**

Spring Security настроен для защиты всех API эндпоинтов:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)  // Отключено для stateless API
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()      // Публичные эндпоинты
            .requestMatchers("/api/**").authenticated()   // Требуют аутентификацию
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Без сессий
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

#### 5. **Дополнительные меры безопасности**

- **Stateless архитектура:** Сервер не хранит сессии, вся информация в JWT-токене
- **Срок действия токена:** 24 часа (настраивается в `application.properties`)
- **Безопасный секретный ключ:** Base64-encoded ключ для подписи токенов (256 бит)
- **Проверка существования пользователя:** При регистрации проверяется уникальность username и email

## Остановка приложения

Для остановки приложения нажмите `Ctrl+C` в терминале.

Для остановки PostgreSQL:

```bash
docker-compose down
```

Для полного удаления данных:

```bash
docker-compose down -v
```

## Дополнительная информация

- Логи приложения доступны в консоли
- SQL запросы выводятся в лог для отладки (можно отключить в application.properties)
- JWT секретный ключ и срок действия токена настраиваются в application.properties

