package STP.GamePlatform.Config;

import STP.GamePlatform.Entities.Game;
import STP.GamePlatform.Entities.GamePlatform;
import STP.GamePlatform.Entities.Platform;
import STP.GamePlatform.Reprsitories.GamePlatformRepository;
import STP.GamePlatform.Reprsitories.GameRepository;
import STP.GamePlatform.Reprsitories.PlatformRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlatformRepository platformRepository;
    private final GamePlatformRepository gamePlatformRepository;
    private final GameRepository gameRepository;

    public DataInitializer(PlatformRepository platformRepository,
                           GamePlatformRepository gamePlatformRepository,
                           GameRepository gameRepository) {
        this.platformRepository = platformRepository;
        this.gamePlatformRepository = gamePlatformRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создание Platform
        Platform steam = Platform.builder()
                .name("Steam")
                .characteristic("Digital distribution platform for PC gaming")
                .urlImage("https://example.com/images/steam.png")
                .build();

        Platform origin = Platform.builder()
                .name("Origin")
                .characteristic("EA's digital distribution platform")
                .urlImage("https://example.com/images/origin.png")
                .build();

        // Сохранение Platform и получение сохранённых объектов с установленными id
        List<Platform> savedPlatforms = platformRepository.saveAll(List.of(steam, origin));

        // Получение обновлённых объектов
        Platform savedSteam = savedPlatforms.stream()
                .filter(p -> p.getName().equals("Steam"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Steam platform not found"));

        Platform savedOrigin = savedPlatforms.stream()
                .filter(p -> p.getName().equals("Origin"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Origin platform not found"));

        // Создание GamePlatforms для Steam
        GamePlatform steamWindows = GamePlatform.builder()
                .name("SteamWindows")
                .urlImage("https://example.com/images/steamwindows.png")
                .platform(savedSteam)
                .build();

        GamePlatform steamMac = GamePlatform.builder()
                .name("SteamMac")
                .urlImage("https://example.com/images/steammac.png")
                .platform(savedSteam)
                .build();

        // Создание GamePlatforms для Origin
        GamePlatform originWindows = GamePlatform.builder()
                .name("OriginWindows")
                .urlImage("https://example.com/images/originwindows.png")
                .platform(savedOrigin)
                .build();

        // Сохранение GamePlatforms и получение сохранённых объектов с установленными id
        List<GamePlatform> savedGamePlatforms = gamePlatformRepository.saveAll(List.of(steamWindows, steamMac, originWindows));

        // Получение обновлённых объектов (опционально, если требуется)
        GamePlatform savedSteamWindows = savedGamePlatforms.stream()
                .filter(gp -> gp.getName().equals("SteamWindows"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SteamWindows GamePlatform not found"));

        GamePlatform savedSteamMac = savedGamePlatforms.stream()
                .filter(gp -> gp.getName().equals("SteamMac"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SteamMac GamePlatform not found"));

        GamePlatform savedOriginWindows = savedGamePlatforms.stream()
                .filter(gp -> gp.getName().equals("OriginWindows"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("OriginWindows GamePlatform not found"));

        // Создание Games без привязок
        Game game1 = Game.builder()
                .name("Dota 2")
                .genre("MOBA")
                .releaseDate("2013-07-09")
                .cover("https://example.com/covers/dota2.jpg")
                .score(9.5)
                .gamePlatforms(new HashSet<>())
                .build();

        Game game2 = Game.builder()
                .name("Stardew Valley")
                .genre("Simulation")
                .releaseDate("2016-02-26")
                .cover("https://example.com/covers/stardewvalley.jpg")
                .score(8.5)
                .gamePlatforms(new HashSet<>())
                .build();

        Game game3 = Game.builder()
                .name("Counter-Strike: Global Offensive")
                .genre("FPS")
                .releaseDate("2012-08-21")
                .cover("https://example.com/covers/csgo.jpg")
                .score(9.0)
                .gamePlatforms(new HashSet<>())
                .build();

        Game game4 = Game.builder()
                .name("Battlefield V")
                .genre("FPS")
                .releaseDate("2018-11-20")
                .cover("https://example.com/covers/bfv.jpg")
                .score(7.8)
                .gamePlatforms(new HashSet<>())
                .build();

        // Добавленные игры без привязок
        Game game5 = Game.builder()
                .name("The Witcher 3: Wild Hunt")
                .genre("RPG")
                .releaseDate("2015-05-19")
                .cover("https://example.com/covers/witcher3.jpg")
                .score(9.8)
                .gamePlatforms(new HashSet<>())
                .build();

        Game game6 = Game.builder()
                .name("Among Us")
                .genre("Party")
                .releaseDate("2018-06-15")
                .cover("https://example.com/covers/amongus.jpg")
                .score(8.0)
                .gamePlatforms(new HashSet<>())
                .build();

        Game game7 = Game.builder()
                .name("Cyberpunk 2077")
                .genre("RPG")
                .releaseDate("2020-12-10")
                .cover("https://example.com/covers/cyberpunk2077.jpg")
                .score(7.5)
                .gamePlatforms(new HashSet<>())
                .build();

        // Сохранение Games
        List<Game> savedGames = gameRepository.saveAll(List.of(game1, game2, game3, game4, game5, game6, game7));

        // Привязка игр к GamePlatform
        savedGames.get(0).getGamePlatforms().add(savedSteamWindows);
        savedGames.get(1).getGamePlatforms().add(savedSteamMac);
        savedGames.get(2).getGamePlatforms().add(savedSteamWindows);
        savedGames.get(3).getGamePlatforms().add(savedOriginWindows);

        // Обновление игр с новыми связями
        gameRepository.saveAll(savedGames);

        System.out.println("Данные успешно инициализированы с новыми связями и дополнительными играми без привязок!");
    }
}