package com.gamer.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gamers")
public class Gamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;  // Только ник, без ФИО

    @Column(unique = true)
    private String email;

    private String displayName;

    private String avatarUrl;

    @Column(unique = true)
    private String externalId;

    private String platform;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    private LocalDateTime lastLogin;

    private Integer totalAchievements = 0;
    private Integer totalGames = 0;
    private Integer totalPlaytime = 0;

    // Связь с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Конструкторы
    public Gamer() {}

    public Gamer(String username, String email, String platform) {
        this.username = username;
        this.email = email;
        this.platform = platform;
        this.registeredAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры остаются без изменений
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Integer getTotalAchievements() { return totalAchievements; }
    public void setTotalAchievements(Integer totalAchievements) { this.totalAchievements = totalAchievements; }

    public Integer getTotalGames() { return totalGames; }
    public void setTotalGames(Integer totalGames) { this.totalGames = totalGames; }

    public Integer getTotalPlaytime() { return totalPlaytime; }
    public void setTotalPlaytime(Integer totalPlaytime) { this.totalPlaytime = totalPlaytime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}