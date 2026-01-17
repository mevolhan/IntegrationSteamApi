package com.gamer.tracker.config;

import com.gamer.tracker.entity.Gamer;
import com.gamer.tracker.entity.User;
import com.gamer.tracker.repository.GamerRepository;
import com.gamer.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GamerRepository gamerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Проверка базы данных Achievement Tracker...");

        try {
            // Проверяем таблицу gamers
            Integer gamersCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'gamers'",
                    Integer.class);

            if (gamersCount > 0) {
                System.out.println("Таблица 'gamers' существует");
            } else {
                System.out.println("⚠Таблица 'gamers' не существует, Hibernate создаст ее");
            }

            // Проверяем таблицу users
            Integer usersCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'",
                    Integer.class);

            if (usersCount > 0) {
                System.out.println("Таблица 'users' существует");
            } else {
                System.out.println("⚠Таблица 'users' не существует, Hibernate создаст ее");
            }

            // Простой запрос для проверки подключения
            String result = jdbcTemplate.queryForObject("SELECT 'PostgreSQL подключена'", String.class);
            System.out.println(result);

            // Создаем администратора (обязательно первым)
            createAdminUser();

            // Создаем тестовые данные игроков, если таблица пуста
            if (gamerRepository.count() == 0) {
                createDemoData();
            }

            System.out.println("Инициализация базы данных завершена!");

        } catch (Exception e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAdminUser() {
        try {
            // Проверяем, существует ли администратор
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@achievement-tracker.ru");
                admin.setPassword(hashPassword("Admin123!")); // Хэшируем пароль
                admin.setSteamId("76561198888888888"); // Демо Steam ID для администратора
                admin.setRole("ADMIN");
                admin.setEnabled(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setLastLogin(LocalDateTime.now());

                userRepository.save(admin);
                System.out.println("Администратор создан:");
                System.out.println("   Логин: admin");
                System.out.println("   Пароль: Admin123!");
                System.out.println("   Email: admin@achievement-tracker.ru");
                System.out.println("   Роль: ADMIN");
            } else {
                System.out.println("Администратор уже существует");
            }

            // Создаем тестового пользователя Danil
            if (!userRepository.existsByUsername("Danil")) {
                User danil = new User();
                danil.setUsername("Danil");
                danil.setEmail("danil@example.com");
                danil.setPassword(hashPassword("Danil123"));
                danil.setRole("USER");
                danil.setEnabled(true);
                danil.setCreatedAt(LocalDateTime.now().minusDays(7));
                danil.setLastLogin(LocalDateTime.now());

                userRepository.save(danil);
                System.out.println("Тестовый пользователь создан:");
                System.out.println("   Логин: Danil");
                System.out.println("   Пароль: Danil123");
            }

        } catch (Exception e) {
            System.err.println("Ошибка создания пользователей: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple encoding if SHA-256 is not available
            return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void createDemoData() {
        System.out.println("Создание демонстрационных данных игроков...");

        List<Gamer> demoGamers = List.of(
                new Gamer("CyberWarrior", "cyber@example.com", "STEAM"),
                new Gamer("XboxMaster", "xbox@example.com", "XBOX"),
                new Gamer("PlayStationPro", "ps@example.com", "PSN"),
                new Gamer("SteamLegend", "steam@example.com", "STEAM"),
                new Gamer("GameFan", "fan@example.com", "EPIC"),
                new Gamer("NintendoSwitch", "switch@example.com", "NINTENDO"),
                new Gamer("ProGamer", "pro@example.com", "STEAM"),
                new Gamer("CasualPlayer", "casual@example.com", "XBOX")
        );

        // Настраиваем демо-данные
        demoGamers.get(0).setDisplayName("CyberKnight");
        demoGamers.get(0).setTotalAchievements(847);
        demoGamers.get(0).setTotalGames(42);
        demoGamers.get(0).setTotalPlaytime(1254);
        demoGamers.get(0).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=CyberWarrior");
        demoGamers.get(0).setExternalId("76561198888888888");

        demoGamers.get(1).setDisplayName("XboxChampion");
        demoGamers.get(1).setTotalAchievements(623);
        demoGamers.get(1).setTotalGames(35);
        demoGamers.get(1).setTotalPlaytime(892);
        demoGamers.get(1).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=XboxMaster");
        demoGamers.get(1).setExternalId("XboxMasterPro");

        demoGamers.get(2).setDisplayName("PSNExpert");
        demoGamers.get(2).setTotalAchievements(451);
        demoGamers.get(2).setTotalGames(28);
        demoGamers.get(2).setTotalPlaytime(567);
        demoGamers.get(2).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=PlayStationPro");
        demoGamers.get(2).setExternalId("PSN_Pro_Player");

        demoGamers.get(3).setDisplayName("SteamKing");
        demoGamers.get(3).setTotalAchievements(1024);
        demoGamers.get(3).setTotalGames(67);
        demoGamers.get(3).setTotalPlaytime(1890);
        demoGamers.get(3).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=SteamLegend");
        demoGamers.get(3).setExternalId("76561197777777777");

        demoGamers.get(4).setDisplayName("EpicGamer");
        demoGamers.get(4).setTotalAchievements(289);
        demoGamers.get(4).setTotalGames(19);
        demoGamers.get(4).setTotalPlaytime(345);
        demoGamers.get(4).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=GameFan");
        demoGamers.get(4).setExternalId("EpicFan_2024");

        demoGamers.get(5).setDisplayName("SwitchPlayer");
        demoGamers.get(5).setTotalAchievements(156);
        demoGamers.get(5).setTotalGames(12);
        demoGamers.get(5).setTotalPlaytime(234);
        demoGamers.get(5).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=NintendoSwitch");
        demoGamers.get(5).setExternalId("SW_789456123");

        demoGamers.get(6).setDisplayName("Professional");
        demoGamers.get(6).setTotalAchievements(753);
        demoGamers.get(6).setTotalGames(51);
        demoGamers.get(6).setTotalPlaytime(987);
        demoGamers.get(6).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=ProGamer");
        demoGamers.get(6).setExternalId("76561196666666666");

        demoGamers.get(7).setDisplayName("Casual Gamer");
        demoGamers.get(7).setTotalAchievements(87);
        demoGamers.get(7).setTotalGames(8);
        demoGamers.get(7).setTotalPlaytime(123);
        demoGamers.get(7).setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=CasualPlayer");
        demoGamers.get(7).setExternalId("Xbox_Casual_001");

        // Устанавливаем время регистрации в прошлом
        for (int i = 0; i < demoGamers.size(); i++) {
            demoGamers.get(i).setRegisteredAt(LocalDateTime.now().minusDays(30 + i * 10));
            demoGamers.get(i).setLastLogin(LocalDateTime.now().minusDays(i * 2));
        }

        gamerRepository.saveAll(demoGamers);
        System.out.println("Создано " + demoGamers.size() + " демонстрационных игроков");
    }
}