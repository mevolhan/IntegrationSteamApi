package com.gamer.tracker.repository;

import com.gamer.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findBySteamId(String steamId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsBySteamId(String steamId);
}