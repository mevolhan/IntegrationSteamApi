package com.gamer.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "steam_app_id", unique = true)
    private Long steamAppId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "header_image_url")
    private String headerImageUrl;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "developers")
    private String developers; // Можно хранить как JSON или список через запятую

    @Column(name = "publishers")
    private String publishers;

    @Column(name = "price_amount")
    private Double priceAmount;

    @Column(name = "price_currency")
    private String priceCurrency;

    @Column(name = "metacritic_score")
    private Integer metacriticScore;

    @Column(name = "recommendations")
    private Integer recommendations;

    @Column(name = "total_achievements")
    private Integer totalAchievements = 0;

    @Column(name = "average_playtime")
    private Integer averagePlaytime;

    @Column(name = "last_sync")
    private LocalDateTime lastSync;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Конструкторы
    public Game() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Game(Long steamAppId, String name) {
        this();
        this.steamAppId = steamAppId;
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSteamAppId() {
        return steamAppId;
    }

    public void setSteamAppId(Long steamAppId) {
        this.steamAppId = steamAppId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public Double getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(Double priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public Integer getMetacriticScore() {
        return metacriticScore;
    }

    public void setMetacriticScore(Integer metacriticScore) {
        this.metacriticScore = metacriticScore;
    }

    public Integer getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(Integer recommendations) {
        this.recommendations = recommendations;
    }

    public Integer getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(Integer totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public Integer getAveragePlaytime() {
        return averagePlaytime;
    }

    public void setAveragePlaytime(Integer averagePlaytime) {
        this.averagePlaytime = averagePlaytime;
    }

    public LocalDateTime getLastSync() {
        return lastSync;
    }

    public void setLastSync(LocalDateTime lastSync) {
        this.lastSync = lastSync;
    }


    }

