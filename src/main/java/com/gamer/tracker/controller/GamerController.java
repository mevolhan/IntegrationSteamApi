package com.gamer.tracker.controller;

import com.gamer.tracker.entity.Gamer;
import com.gamer.tracker.service.GamerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/gamers")
public class GamerController {

    @Autowired
    private GamerService gamerService;

    @GetMapping
    public String listGamers(@RequestParam(required = false) String platform,
                             @RequestParam(required = false) String search,
                             HttpSession session,
                             Model model) {

        // Добавляем информацию о пользователе в модель
        addUserInfoToModel(session, model);

        List<Gamer> gamers;

        // Устанавливаем значения по умолчанию
        String selectedPlatform = (platform != null && !platform.isEmpty()) ? platform : "ALL";
        String searchQuery = (search != null) ? search : "";

        if (selectedPlatform != null && !selectedPlatform.isEmpty() && !selectedPlatform.equals("ALL")) {
            // Фильтрация по платформе
            gamers = gamerService.getGamersByPlatform(selectedPlatform);
        } else if (searchQuery != null && !searchQuery.isEmpty()) {
            // Поиск по имени
            gamers = gamerService.searchGamers(searchQuery);
        } else {
            // Все игроки
            gamers = gamerService.getAllGamers();
        }

        model.addAttribute("gamers", gamers);
        model.addAttribute("totalGamers", gamers.size());
        model.addAttribute("platforms", Arrays.asList("ALL", "STEAM", "XBOX", "PSN", "EPIC", "NINTENDO"));
        model.addAttribute("selectedPlatform", selectedPlatform);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("page", "gamers");

        return "gamers/list";
    }

    @GetMapping("/platform/{platform}")
    public String gamersByPlatform(@PathVariable String platform,
                                   HttpSession session,
                                   Model model) {
        addUserInfoToModel(session, model);

        List<Gamer> gamers = gamerService.getGamersByPlatform(platform);
        model.addAttribute("gamers", gamers);
        model.addAttribute("totalGamers", gamers.size());
        model.addAttribute("platform", platform);
        return "gamers/platform-list";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login?redirect=/gamers/create";
        }

        addUserInfoToModel(session, model);

        model.addAttribute("gamer", new Gamer());
        model.addAttribute("platforms", Arrays.asList("STEAM", "XBOX", "PSN", "EPIC", "NINTENDO"));
        return "gamers/create";
    }

    @PostMapping("/create")
    public String createGamer(@ModelAttribute Gamer gamer, HttpSession session) {
        // Проверяем авторизацию
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        gamerService.createGamer(gamer);
        return "redirect:/gamers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        addUserInfoToModel(session, model);

        Gamer gamer = gamerService.getGamerById(id);
        model.addAttribute("gamer", gamer);
        model.addAttribute("platforms", Arrays.asList("STEAM", "XBOX", "PSN", "EPIC", "NINTENDO"));
        return "gamers/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateGamer(@PathVariable Long id, @ModelAttribute Gamer gamer, HttpSession session) {
        // Проверяем авторизацию
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        gamerService.updateGamer(id, gamer);
        return "redirect:/gamers";
    }

    @GetMapping("/delete/{id}")
    public String deleteGamer(@PathVariable Long id, HttpSession session) {
        // Проверяем авторизацию
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login";
        }

        gamerService.deleteGamer(id);
        return "redirect:/gamers";
    }

    @GetMapping("/{id}")
    public String viewGamer(@PathVariable Long id, HttpSession session, Model model) {
        addUserInfoToModel(session, model);

        Gamer gamer = gamerService.getGamerById(id);
        model.addAttribute("gamer", gamer);
        return "gamers/view";
    }

    // Вспомогательный метод для добавления информации о пользователе в модель
    private void addUserInfoToModel(HttpSession session, Model model) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("authenticated");
        if (isAuthenticated != null && isAuthenticated) {
            String username = (String) session.getAttribute("username");
            model.addAttribute("username", username);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
}