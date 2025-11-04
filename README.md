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

При первом запуске автоматически создаются тестовые пользователи:

| Username | Password    | Role  |
|----------|-------------|-------|
| admin    | admin123    | ADMIN |
| john     | password123 | USER  |
| jane     | password123 | USER  |

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

- Пароли хранятся в зашифрованном виде с использованием BCrypt
- JWT токены имеют срок действия 24 часа
- Все защищенные эндпоинты требуют валидный JWT токен в заголовке Authorization
- CSRF защита отключена (stateless API)

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

