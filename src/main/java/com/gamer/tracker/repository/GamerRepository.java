package com.gamer.tracker.repository;

import com.gamer.tracker.entity.Gamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Long> {

    Optional<Gamer> findByUsername(String username);
    Optional<Gamer> findByEmail(String email);
    Optional<Gamer> findByExternalId(String externalId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Новые методы для фильтрации
    List<Gamer> findByPlatform(String platform);

    @Query("SELECT g FROM Gamer g WHERE LOWER(g.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(g.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Gamer> findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            @Param("searchTerm") String searchTerm1,
            @Param("searchTerm") String searchTerm2);

    // Статистика по платформам
    @Query("SELECT g.platform, COUNT(g) FROM Gamer g GROUP BY g.platform")
    List<Object[]> countByPlatform();
}