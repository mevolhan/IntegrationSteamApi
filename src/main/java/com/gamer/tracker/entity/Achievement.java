package com.gamer.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "steam_api_name", nullable = false)
    private String steamApiName; // Имя достижения в Steam API

    @Column(name = "display_name", nullable = false)
    private String displayName; // Отображаемое имя

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "icon_gray_url")
    private String iconGrayUrl;

    @Column(name = "achieved_icon_url")
    private String achievedIconUrl;

    @Column(name = "hidden")
    private Boolean hidden = false; // Скрытое достижение

    @Column(name = "global_percentage")
    private Double globalPercentage; // Процент игроков, получивших достижение

    @Column(name = "points")
    private Integer points; // Очки за достижение (например, для Xbox)

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Конструкторы
    public Achievement() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Achievement(Game game, String steamApiName, String displayName) {
        this();
        this.game = game;
        this.steamApiName = steamApiName;
        this.displayName = displayName;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getSteamApiName() {
        return steamApiName;
    }

    public void setSteamApiName(String steamApiName) {
        this.steamApiName = steamApiName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconGrayUrl() {
        return iconGrayUrl;
    }

    public void setIconGrayUrl(String iconGrayUrl) {
        this.iconGrayUrl = iconGrayUrl;
    }

    public String getAchievedIconUrl() {
        return achievedIconUrl;
    }

    public void setAchievedIconUrl(String achievedIconUrl) {
        this.achievedIconUrl = achievedIconUrl;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Double getGlobalPercentage() {
        return globalPercentage;
    }

    public void setGlobalPercentage(Double globalPercentage) {
        this.globalPercentage = globalPercentage;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDateTime getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(LocalDateTime unlockTime) {
        this.unlockTime = unlockTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Методы для обновления времени
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Вспомогательные методы
    public boolean isUnlocked() {
        return unlockTime != null;
    }

    public String getIconBasedOnStatus() {
        if (isUnlocked() && achievedIconUrl != null) {
            return achievedIconUrl;
        }
        return iconUrl != null ? iconUrl : iconGrayUrl;
    }
}