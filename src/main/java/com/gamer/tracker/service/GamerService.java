package com.gamer.tracker.service;

import com.gamer.tracker.entity.Gamer;
import com.gamer.tracker.repository.GamerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GamerService {

    @Autowired
    private GamerRepository gamerRepository;

    public Gamer createGamer(Gamer gamer) {
        gamer.setRegisteredAt(LocalDateTime.now());
        return gamerRepository.save(gamer);
    }

    public List<Gamer> getAllGamers() {
        return gamerRepository.findAll();
    }

    public Gamer getGamerById(Long id) {
        return gamerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Игрок не найден с id: " + id));
    }

    public Gamer updateGamer(Long id, Gamer gamerDetails) {
        Gamer gamer = getGamerById(id);
        gamer.setUsername(gamerDetails.getUsername());
        gamer.setEmail(gamerDetails.getEmail());
        gamer.setDisplayName(gamerDetails.getDisplayName());
        gamer.setAvatarUrl(gamerDetails.getAvatarUrl());
        gamer.setExternalId(gamerDetails.getExternalId());
        gamer.setPlatform(gamerDetails.getPlatform());
        gamer.setLastLogin(LocalDateTime.now());
        return gamerRepository.save(gamer);
    }

    public void deleteGamer(Long id) {
        Gamer gamer = getGamerById(id);
        gamerRepository.delete(gamer);
    }

    public Gamer findByUsername(String username) {
        return gamerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Игрок не найден с именем: " + username));
    }

    // Новые методы для фильтрации
    public List<Gamer> getGamersByPlatform(String platform) {
        return gamerRepository.findByPlatform(platform);
    }

    public List<Gamer> searchGamers(String searchTerm) {
        return gamerRepository.findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                searchTerm, searchTerm);
    }
}