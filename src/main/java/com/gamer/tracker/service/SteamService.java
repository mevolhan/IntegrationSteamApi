package com.gamer.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class SteamService {

    @Value("${steam.api.key}")
    private String steamApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Получение информации о пользователе Steam по SteamID
     */
    public Map<String, Object> getPlayerSummary(String steamId) {
        try {
            System.out.println("Запрос к Steam API для SteamID: " + steamId);

            String url = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamids", steamId)
                    .toUriString();

            System.out.println("URL запроса: " + url.replace(steamApiKey, "***HIDDEN***"));

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().isError()) {
                throw new RuntimeException("Steam API вернул ошибку: " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("response")) {
                throw new RuntimeException("Неверный ответ от Steam API");
            }

            Map<String, Object> responseMap = (Map<String, Object>) body.get("response");
            if (responseMap.get("players") == null || ((java.util.List) responseMap.get("players")).isEmpty()) {
                throw new RuntimeException("Steam пользователь не найден. Проверьте Steam ID");
            }

            System.out.println("Успешный ответ от Steam API");
            return body;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new RuntimeException("Неверный Steam API ключ. Получите ключ на https://steamcommunity.com/dev/apikey и добавьте в application.yml");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Steam пользователь не найден. Проверьте правильность Steam ID");
            }
            throw new RuntimeException("Ошибка Steam API: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подключения к Steam API: " + e.getMessage());
        }
    }

    /**
     * Извлекает SteamID из URL или принимает его напрямую
     */
    public String extractSteamId(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new RuntimeException("Steam ID не может быть пустым");
        }

        // Если это URL, извлекаем SteamID или никнейм
        if (input.contains("steamcommunity.com")) {
            // Извлекаем никнейм из URL вида https://steamcommunity.com/id/mevolhan/
            if (input.contains("/id/")) {
                String[] parts = input.split("/id/");
                if (parts.length > 1) {
                    String nickname = parts[1].replace("/", "").trim();
                    System.out.println("Извлечен никнейм из URL: " + nickname);
                    return resolveVanityUrl(nickname);
                }
            }
            // Извлекаем SteamID из URL вида https://steamcommunity.com/profiles/76561197960435530/
            else if (input.contains("/profiles/")) {
                String[] parts = input.split("/profiles/");
                if (parts.length > 1) {
                    String steamId = parts[1].replace("/", "").trim();
                    System.out.println("Извлечен SteamID из URL: " + steamId);
                    return steamId;
                }
            }
            throw new RuntimeException("Неверный формат Steam URL. Используйте формат: https://steamcommunity.com/id/ваш_никнейм или https://steamcommunity.com/profiles/ваш_steamid");
        }

        // Если это цифровой SteamID (начинается с 7656119)
        if (input.matches("7656119\\d+")) {
            return input;
        }

        // Иначе считаем, что это никнейм и пытаемся разрешить
        System.out.println("Разрешение SteamID для никнейма: " + input);
        return resolveVanityUrl(input);
    }

    /**
     * Поиск SteamID по пользовательскому URL (никнейму)
     */
    public String resolveVanityUrl(String vanityUrl) {
        try {
            // Очищаем никнейм от лишних символов
            String cleanVanityUrl = vanityUrl.replace("https://steamcommunity.com/id/", "")
                    .replace("/", "")
                    .trim();

            String url = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/")
                    .queryParam("key", steamApiKey)
                    .queryParam("vanityurl", cleanVanityUrl)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                Map<String, Object> responseMap = (Map<String, Object>) body.get("response");
                if (responseMap.get("success") != null && (Integer) responseMap.get("success") == 1) {
                    String steamId = (String) responseMap.get("steamid");
                    System.out.println("Найден SteamID: " + steamId + " для никнейма: " + cleanVanityUrl);
                    return steamId;
                } else {
                    throw new RuntimeException("Не удалось найти SteamID для никнейма: " + cleanVanityUrl + ". Проверьте правильность никнейма.");
                }
            }

            throw new RuntimeException("Не удалось найти SteamID для: " + vanityUrl);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new RuntimeException("Неверный Steam API ключ. Проверьте ключ в application.yml");
            }
            throw new RuntimeException("Ошибка Steam API: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска SteamID: " + e.getMessage());
        }
    }

    /**
     * Получение списка игр пользователя
     */
    public Map<String, Object> getOwnedGames(String steamId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamid", steamId)
                    .queryParam("include_appinfo", "true")
                    .queryParam("include_played_free_games", "true")
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return createErrorResponse("Ошибка получения списка игр: " + e.getMessage());
        }
    }

    /**
     * Проверка валидности Steam API ключа
     */
    public boolean validateApiKey() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamids", "76561197960435530")
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    /**
     * Получение информации о Steam API статусе
     */
    public Map<String, Object> getApiStatus() {
        Map<String, Object> status = new HashMap<>();

        boolean isKeyValid = validateApiKey();
        status.put("apiKeyValid", isKeyValid);
        status.put("apiKey", steamApiKey != null && !steamApiKey.equals("YOUR_STEAM_API_KEY_HERE")
                ? "***" + steamApiKey.substring(steamApiKey.length() - 4)
                : "Не установлен");

        if (!isKeyValid) {
            status.put("message", "Требуется действительный Steam API ключ. Получите на https://steamcommunity.com/dev/apikey");
        }

        return status;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        return errorResponse;
    }
}