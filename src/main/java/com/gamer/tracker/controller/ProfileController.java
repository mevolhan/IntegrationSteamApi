package com.gamer.tracker.controller;

import com.gamer.tracker.entity.User;
import com.gamer.tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        userService.updateLastLogin(username);
        User user = userService.getUserByUsername(username);
        Map<String, Object> stats = userService.getUserStats(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("stats", stats);
        model.addAttribute("pageTitle", "Мой профиль");
        model.addAttribute("page", "profile"); // ← добавлено

        if (session.getAttribute("steamConnected") != null) {
            model.addAttribute("steamConnected", true);
            session.removeAttribute("steamConnected");
        }
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Редактирование профиля");
        model.addAttribute("page", "profile-edit"); // ← добавлено
        return "profile/edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String email,
                                @RequestParam(required = false) String currentPassword,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword,
                                HttpSession session,
                                Model model) {
        try {
            String username = (String) session.getAttribute("username");

            // Валидация нового пароля
            if (newPassword != null && !newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    throw new RuntimeException("Новый пароль должен быть не менее 6 символов");
                }
                if (!newPassword.equals(confirmPassword)) {
                    throw new RuntimeException("Новые пароли не совпадают");
                }
            }

            // Обновляем профиль
            User updatedUser = userService.updateProfile(username, email, currentPassword, newPassword);

            model.addAttribute("success", "Профиль успешно обновлен");
            model.addAttribute("user", updatedUser);

            return "redirect:/profile?updated=true";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка обновления: " + e.getMessage());
            return editProfilePage(session, model);
        }
    }

    @GetMapping("/profile/steam")
    public String steamProfilePage(HttpSession session, Model model) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");
        User user = userService.getUserByUsername(username);

        if (user.getSteamId() == null || user.getSteamId().isEmpty()) {
            return "redirect:/steam/connect";
        }

        return "redirect:/steam/profile/" + user.getSteamId();
    }
}