package com.gamer.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "player_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"gamer_id", "game_id"}))
public class PlayerProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "total_playtime")
    private Integer totalPlaytime = 0; // В минутах

    @Column(name = "achievements_unlocked")
    private Integer achievementsUnlocked = 0;

    @Column(name = "total_achievements")
    private Integer totalAchievements = 0;

    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    @Column(name = "last_played")
    private LocalDateTime lastPlayed;

    @Column(name = "first_played")
    private LocalDateTime firstPlayed;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @Column(name = "personal_rating")
    private Integer personalRating; // Оценка игрока от 1 до 10

    @Column(name = "notes")
    private String notes;

    @ManyToMany
    @JoinTable(
            name = "player_achievements",
            joinColumns = @JoinColumn(name = "player_progress_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private Set<Achievement> unlockedAchievements = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Конструкторы
    public PlayerProgress() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.firstPlayed = LocalDateTime.now();
    }

    public PlayerProgress(Gamer gamer, Game game) {
        this();
        this.gamer = gamer;
        this.game = game;
        this.totalAchievements = game.getTotalAchievements();
        updateCompletionPercentage();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gamer getGamer() {
        return gamer;
    }

    public void setGamer(Gamer gamer) {
        this.gamer = gamer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        this.totalAchievements = game.getTotalAchievements();
        updateCompletionPercentage();
    }

    public Integer getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(Integer totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public Integer getAchievementsUnlocked() {
        return achievementsUnlocked;
    }

    public void setAchievementsUnlocked(Integer achievementsUnlocked) {
        this.achievementsUnlocked = achievementsUnlocked;
        updateCompletionPercentage();
    }

    public Integer getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(Integer totalAchievements) {
        this.totalAchievements = totalAchievements;
        updateCompletionPercentage();
    }

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public LocalDateTime getFirstPlayed() {
        return firstPlayed;
    }

    public void setFirstPlayed(LocalDateTime firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Integer getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(Integer personalRating) {
        this.personalRating = personalRating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Achievement> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(Set<Achievement> unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
        this.achievementsUnlocked = unlockedAchievements.size();
        updateCompletionPercentage();
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
    private void updateCompletionPercentage() {
        if (totalAchievements > 0) {
            this.completionPercentage = (double) achievementsUnlocked / totalAchievements * 100;
        } else {
            this.completionPercentage = 0.0;
        }
    }

    public void unlockAchievement(Achievement achievement) {
        if (!unlockedAchievements.contains(achievement)) {
            unlockedAchievements.add(achievement);
            achievementsUnlocked++;
            updateCompletionPercentage();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void lockAchievement(Achievement achievement) {
        if (unlockedAchievements.contains(achievement)) {
            unlockedAchievements.remove(achievement);
            achievementsUnlocked--;
            updateCompletionPercentage();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasAchievement(Achievement achievement) {
        return unlockedAchievements.contains(achievement);
    }

    public void addPlaytime(int minutes) {
        this.totalPlaytime += minutes;
        this.lastPlayed = LocalDateTime.now();
    }

    // Форматированные значения для отображения
    public String getFormattedPlaytime() {
        if (totalPlaytime < 60) {
            return totalPlaytime + " мин";
        } else if (totalPlaytime < 1440) {
            return (totalPlaytime / 60) + " ч " + (totalPlaytime % 60) + " мин";
        } else {
            return (totalPlaytime / 1440) + " д " + ((totalPlaytime % 1440) / 60) + " ч";
        }
    }

    public String getCompletionPercentageFormatted() {
        return String.format("%.1f%%", completionPercentage);
    }
}