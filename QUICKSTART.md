# Быстрый старт

## 1. Запуск PostgreSQL

```bash
docker-compose up -d
```

Проверка запуска:
```bash
docker ps
```

## 2. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение запустится на `http://localhost:8080`

## 3. Тестирование API

### Вход в систему

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Сохраните полученный токен.

### Получение данных

```bash
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer ВАШ_ТОКЕН"
```

### Создание поста

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ВАШ_ТОКЕН" \
  -d '{"title": "Новый пост", "content": "Содержимое"}'
```

## Тестовые пользователи

| Username | Password    | Role  |
|----------|-------------|-------|
| admin    | admin123    | ADMIN |
| john     | password123 | USER  |
| jane     | password123 | USER  |

## Остановка

```bash
# Остановить приложение: Ctrl+C

# Остановить PostgreSQL
docker-compose down
```

