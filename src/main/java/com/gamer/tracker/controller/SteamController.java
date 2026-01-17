package com.gamer.tracker.controller;

import com.gamer.tracker.service.SteamService;
import com.gamer.tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/steam")
public class SteamController {

    @Autowired
    private SteamService steamService;

    @Autowired
    private UserService userService;

    @GetMapping("/connect")
    public String connectPage(HttpSession session, Model model) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Подключение Steam");
        model.addAttribute("page", "steam"); // ← добавлено

        Map<String, Object> apiStatus = steamService.getApiStatus();
        model.addAttribute("apiStatus", apiStatus);

        boolean isSteamConnected = userService.isSteamConnected(username);
        model.addAttribute("isSteamConnected", isSteamConnected);
        if (isSteamConnected) {
            model.addAttribute("steamId", userService.getUserByUsername(username).getSteamId());
        }
        return "steam/connect";
    }

    @PostMapping("/connect")
    public String connectSteam(@RequestParam(required = false) String steamInput,
                               HttpSession session,
                               Model model) {
        try {
            if (session.getAttribute("authenticated") == null) {
                return "redirect:/login";
            }

            String username = (String) session.getAttribute("username");

            // Проверка пустого ввода
            if (steamInput == null || steamInput.trim().isEmpty()) {
                model.addAttribute("error", "Пожалуйста, введите Steam ID или URL профиля.");
                return connectPage(session, model);
            }

            // Извлекаем SteamID из ввода (URL, никнейм или ID)
            String resolvedSteamId = steamService.extractSteamId(steamInput);

            // Получаем данные игрока — это автоматически проверяет существование
            Map<String, Object> playerData = steamService.getPlayerSummary(resolvedSteamId);

            // Если дошли сюда — всё в порядке, сохраняем связь
            userService.connectSteam(username, resolvedSteamId);

            // Устанавливаем флаг для уведомления
            session.setAttribute("steamConnected", true);

            return "redirect:/profile";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка подключения: " + e.getMessage());
            return connectPage(session, model);
        }
    }

    @GetMapping("/disconnect")
    public String disconnectSteam(HttpSession session) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");
        userService.disconnectSteam(username);

        return "redirect:/profile?steamDisconnected=true";
    }

    @GetMapping("/profile/{steamId}")
    public String steamProfile(@PathVariable String steamId,
                               HttpSession session,
                               Model model) {
        boolean isAuthenticated = session.getAttribute("authenticated") != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        try {
            Map<String, Object> playerData = steamService.getPlayerSummary(steamId);
            Map<String, Object> gamesData = steamService.getOwnedGames(steamId);
            model.addAttribute("playerData", playerData);
            model.addAttribute("gamesData", gamesData);
            model.addAttribute("steamId", steamId);
            model.addAttribute("pageTitle", "Профиль Steam");
            return "steam/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Не удалось загрузить данные Steam: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/api/test")
    @ResponseBody
    public Map<String, Object> testSteamApi(@RequestParam(required = false) String steamId) {
        if (steamId == null || steamId.isEmpty()) {
            return steamService.getApiStatus();
        }
        return steamService.getPlayerSummary(steamId);
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> steamApiStatus() {
        return steamService.getApiStatus();
    }
}