# Achievement Tracker - Steam API Integration

Система отслеживания игровых достижений с интеграцией Steam API.

## Функциональность

###  Реализовано
- **Аутентификация пользователей** (регистрация, вход, профиль)
- **Управление игроками** (CRUD операции, фильтрация)
-  **Таблица лидеров** (сортировка по достижениям)
-  **Интеграция с Steam API**
  - Подключение Steam аккаунта
  - Получение данных профиля
  - Список игр пользователя
- **Современный интерфейс** с использованием Bootstrap 5
-  **Работа с PostgreSQL**

### Сущности системы
- `User` - пользователь системы
- `Gamer` - игровой профиль
- `Game` - информация об игре (из Steam)
- `Achievement` - достижение в игре
- `PlayerProgress` - прогресс игрока в игре

## Технологический стек

| Компонент | Технология |
|-----------|------------|
| **Backend** | Spring Boot 3.2.3, Java 17 |
| **Frontend** | Thymeleaf, Bootstrap 5, JavaScript |
| **Database** | PostgreSQL 15 |
| **API Integration** | Steam Web API |
| **Build Tool** | Maven |
| **Security** | HTTP Session Authentication |
