package com.gamer.tracker.controller;

import com.gamer.tracker.entity.Gamer;
import com.gamer.tracker.entity.User;
import com.gamer.tracker.service.GamerService;
import com.gamer.tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private GamerService gamerService;

    @Autowired
    private UserService userService;

    @GetMapping("/leaderboards")
    public String leaderboardsPage(Model model, HttpSession session) {
        // Получаем всех игроков
        List<Gamer> allGamers = gamerService.getAllGamers();

        // Сортируем по количеству достижений (по убыванию)
        List<Gamer> sortedGamers = allGamers.stream()
                .sorted(Comparator.comparingInt(Gamer::getTotalAchievements).reversed())
                .collect(Collectors.toList());

        // Рассчитываем общую статистику
        int totalAchievements = sortedGamers.stream()
                .mapToInt(Gamer::getTotalAchievements)
                .sum();

        int totalPlaytime = sortedGamers.stream()
                .mapToInt(Gamer::getTotalPlaytime)
                .sum();

        int totalGames = sortedGamers.stream()
                .mapToInt(Gamer::getTotalGames)
                .sum();

        model.addAttribute("gamers", sortedGamers);
        model.addAttribute("totalGamers", sortedGamers.size());
        model.addAttribute("totalAchievements", totalAchievements);
        model.addAttribute("totalPlaytime", totalPlaytime);
        model.addAttribute("totalGames", totalGames);
        model.addAttribute("page", "leaderboards");

        // Проверяем авторизацию
        if (session.getAttribute("authenticated") != null) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("username", username);

            // Проверка подключения Steam для лидербордов
            try {
                User user = userService.getUserByUsername(username);
                boolean steamConnected = user.getSteamId() != null && !user.getSteamId().isEmpty();
                model.addAttribute("steamConnected", steamConnected);
                if (steamConnected) {
                    model.addAttribute("steamId", user.getSteamId());
                }
            } catch (Exception e) {
                // Игнорируем ошибку
            }
        }

        return "leaderboards";
    }

    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {
        boolean isAuthenticated = session.getAttribute("authenticated") != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("username", username);

            // Проверка подключения Steam для главной страницы
            try {
                User user = userService.getUserByUsername(username);
                boolean steamConnected = user.getSteamId() != null && !user.getSteamId().isEmpty();
                model.addAttribute("steamConnected", steamConnected);
                if (steamConnected) {
                    model.addAttribute("steamId", user.getSteamId());
                }
            } catch (Exception e) {
                // Игнорируем ошибку
            }
        }

        // Добавляем текущую страницу
        model.addAttribute("page", "home");

        // Основная информация
        model.addAttribute("title", "Achievement Tracker");
        model.addAttribute("subtitle", "Система отслеживания игровых достижений");

        // API статусы (имитация работы)
        model.addAttribute("steamStatus", "Подключено");
        model.addAttribute("steamPlayers", 1543);
        model.addAttribute("steamGames", 428);

        model.addAttribute("discordStatus", "Активно");
        model.addAttribute("discordMembers", 892);

        model.addAttribute("rabbitmqStatus", "Работает");
        model.addAttribute("rabbitmqMessages", 12458);

        model.addAttribute("dbStatus", "Онлайн");
        model.addAttribute("dbSize", "42.5 MB");

        // Статистика системы
        long totalPlayers = gamerService.getAllGamers().size();
        model.addAttribute("totalPlayers", totalPlayers);
        model.addAttribute("totalGames", 875);
        model.addAttribute("totalAchievements", 12458);
        model.addAttribute("totalPlaytime", 154872);

        // Последние действия
        model.addAttribute("recentActivity", new String[]{
                "Игрок 'CyberWarrior' получил достижение 'Легенда' в Cyberpunk 2077",
                "Синхронизировано 12 новых игр из Steam API",
                "Обновлены данные для 47 игроков",
                "Отправлено 23 уведомления через RabbitMQ"
        });

        return "home";
    }

    @GetMapping("/api-demo")
    public String apiDemoPage(HttpSession session, Model model) {
        boolean isAuthenticated = session.getAttribute("authenticated") != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("title", "Демонстрация API");

        // Проверка подключения Steam для страницы API
        if (isAuthenticated) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("username", username);

            try {
                User user = userService.getUserByUsername(username);
                boolean steamConnected = user.getSteamId() != null && !user.getSteamId().isEmpty();
                model.addAttribute("steamConnected", steamConnected);
                if (steamConnected) {
                    model.addAttribute("steamId", user.getSteamId());
                }
            } catch (Exception e) {
                // Игнорируем ошибку
            }
        }

        return "api-demo";
    }
}