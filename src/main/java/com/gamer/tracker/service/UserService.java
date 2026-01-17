package com.gamer.tracker.service;

import com.gamer.tracker.entity.User;
import com.gamer.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    // Простая реализация хэширования пароля
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple encoding if SHA-256 is not available
            return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        }
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        String hash = hashPassword(rawPassword);
        return hash.equals(hashedPassword);
    }

    public User registerUser(String username, String email, String password) {
        // Проверяем, существует ли пользователь
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Имя пользователя уже занято");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword(password)); // Хэшируем пароль
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User connectSteam(String username, String steamId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем, не привязан ли уже этот Steam ID к другому пользователю
        userRepository.findBySteamId(steamId).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(user.getId())) {
                throw new RuntimeException("Этот Steam аккаунт уже привязан к другому пользователю");
            }
        });

        user.setSteamId(steamId);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public boolean validateUser(String username, String password) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            return checkPassword(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Обновление профиля пользователя
     */
    public User updateProfile(String username, String email, String currentPassword, String newPassword) {
        User user = getUserByUsername(username);

        // Обновляем email если он изменился
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email уже используется другим пользователем");
            }
            user.setEmail(email);
        }

        // Обновляем пароль если предоставлен
        if (newPassword != null && !newPassword.isEmpty()) {
            if (currentPassword == null || currentPassword.isEmpty()) {
                throw new RuntimeException("Требуется текущий пароль для изменения пароля");
            }

            if (!checkPassword(currentPassword, user.getPassword())) {
                throw new RuntimeException("Текущий пароль неверен");
            }

            user.setPassword(hashPassword(newPassword));
        }

        return userRepository.save(user);
    }

    /**
     * Проверка, подключен ли Steam аккаунт
     */
    public boolean isSteamConnected(String username) {
        User user = getUserByUsername(username);
        return user.getSteamId() != null && !user.getSteamId().isEmpty();
    }

    /**
     * Отключение Steam аккаунта
     */
    public User disconnectSteam(String username) {
        User user = getUserByUsername(username);
        user.setSteamId(null);
        return userRepository.save(user);
    }

    /**
     * Получение статистики пользователя
     */
    public Map<String, Object> getUserStats(String username) {
        User user = getUserByUsername(username);
        Map<String, Object> stats = new HashMap<>();

        stats.put("username", user.getUsername());
        stats.put("email", user.getEmail());
        stats.put("role", user.getRole());
        stats.put("steamConnected", user.getSteamId() != null);
        stats.put("steamId", user.getSteamId());
        stats.put("createdAt", user.getCreatedAt());
        stats.put("lastLogin", user.getLastLogin());
        stats.put("isAdmin", user.isAdmin());

        return stats;
    }

    /**
     * Обновление времени последнего входа
     */
    public void updateLastLogin(String username) {
        try {
            User user = getUserByUsername(username);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        } catch (RuntimeException e) {
            // Логируем ошибку, но не прерываем выполнение
            System.err.println("Не удалось обновить время входа для пользователя: " + username);
        }
    }
}

