package STP.GamePlatform.Services;

import STP.GamePlatform.DTO.GameDTO;
import STP.GamePlatform.Entities.Game;
import STP.GamePlatform.Entities.GamePlatform;
import STP.GamePlatform.Reprsitories.GamePlatformRepository;
import STP.GamePlatform.Reprsitories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlatformRepository gamePlatformRepository;

    // Получение всех игр
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение игры по ID
    public Optional<GameDTO> getGameById(Long id) {
        return gameRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Создание новой игры
    public GameDTO createGame(GameDTO gameDTO) {
        Game game = convertToEntity(gameDTO);
        Game savedGame = gameRepository.save(game);
        return convertToDTO(savedGame);
    }

    // Обновление существующей игры
    public Optional<GameDTO> updateGame(Long id, GameDTO gameDTO) {
        return gameRepository.findById(id).map(game -> {
            game.setName(gameDTO.getName());
            game.setGenre(gameDTO.getGenre());
            game.setReleaseDate(gameDTO.getReleaseDate());
            game.setCover(gameDTO.getCover());
            game.setScore(gameDTO.getScore());
            if (gameDTO.getGamePlatformIds() != null) {
                Set<GamePlatform> gamePlatforms = new HashSet<>(gamePlatformRepository.findAllById(gameDTO.getGamePlatformIds()));
                game.setGamePlatforms(gamePlatforms);
            }
            Game updatedGame = gameRepository.save(game);
            return convertToDTO(updatedGame);
        });
    }

    // Удаление игры
    public boolean deleteGame(Long id) {
        return gameRepository.findById(id).map(game -> {
            gameRepository.delete(game);
            return true;
        }).orElse(false);
    }

    // Привязка игры к игровой платформе и возврат обновлённого GameDTO
    public Optional<GameDTO> assignGameToGamePlatform(Long gameId, Long gamePlatformId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        Optional<GamePlatform> gamePlatformOpt = gamePlatformRepository.findById(gamePlatformId);

        if (gameOpt.isPresent() && gamePlatformOpt.isPresent()) {
            Game game = gameOpt.get();
            GamePlatform gamePlatform = gamePlatformOpt.get();

            // Проверка на существование привязки, чтобы избежать дублирования
            if (game.getGamePlatforms().contains(gamePlatform)) {
                return Optional.empty(); // Или выбросить исключение для лучшей обработки ошибок
            }

            // Привязка игры к платформе
            game.getGamePlatforms().add(gamePlatform);
            Game updatedGame = gameRepository.save(game);
            return Optional.of(convertToDTO(updatedGame));
        }
        return Optional.empty();
    }

    // Отвязка игры от игровой платформы
    public boolean unassignGameFromGamePlatform(Long gameId, Long gamePlatformId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        Optional<GamePlatform> gamePlatformOpt = gamePlatformRepository.findById(gamePlatformId);

        if (gameOpt.isPresent() && gamePlatformOpt.isPresent()) {
            Game game = gameOpt.get();
            GamePlatform gamePlatform = gamePlatformOpt.get();

            // Проверка, привязана ли игра к этой платформе
            if (!game.getGamePlatforms().contains(gamePlatform)) {
                return false;
            }

            game.getGamePlatforms().remove(gamePlatform);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    // Конвертация сущности в DTO
    private GameDTO convertToDTO(Game game) {
        Set<Long> gamePlatformIds = game.getGamePlatforms().stream()
                .map(GamePlatform::getId)
                .collect(Collectors.toSet());

        return GameDTO.builder()
                .id(game.getId())
                .name(game.getName())
                .genre(game.getGenre())
                .releaseDate(game.getReleaseDate())
                .cover(game.getCover())
                .score(game.getScore())
                .gamePlatformIds(gamePlatformIds)
                .build();
    }

    // Конвертация DTO в сущность
    private Game convertToEntity(GameDTO gameDTO) {
        Set<GamePlatform> gamePlatforms = gameDTO.getGamePlatformIds() != null ?
                new HashSet<>(gamePlatformRepository.findAllById(gameDTO.getGamePlatformIds())) : new HashSet<>();

        return Game.builder()
                .name(gameDTO.getName())
                .genre(gameDTO.getGenre())
                .releaseDate(gameDTO.getReleaseDate())
                .cover(gameDTO.getCover())
                .score(gameDTO.getScore())
                .gamePlatforms(gamePlatforms)
                .build();
    }
}