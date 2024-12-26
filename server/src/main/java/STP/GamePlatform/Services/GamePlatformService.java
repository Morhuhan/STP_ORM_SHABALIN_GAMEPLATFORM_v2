package STP.GamePlatform.Services;

import STP.GamePlatform.DTO.GamePlatformDTO;
import STP.GamePlatform.Entities.GamePlatform;
import STP.GamePlatform.Entities.Platform;
import STP.GamePlatform.Entities.Game;
import STP.GamePlatform.Reprsitories.GamePlatformRepository;
import STP.GamePlatform.Reprsitories.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GamePlatformService {

    @Autowired
    private GamePlatformRepository gamePlatformRepository;

    @Autowired
    private PlatformRepository platformRepository;

    // Получение всех игровых платформ
    public List<GamePlatformDTO> getAllGamePlatforms() {
        return gamePlatformRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение игровой платформы по ID
    public Optional<GamePlatformDTO> getGamePlatformById(Long id) {
        return gamePlatformRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Создание новой игровой платформы
    public Optional<GamePlatformDTO> createGamePlatform(GamePlatformDTO gamePlatformDTO) {
        Optional<Platform> platformOpt = platformRepository.findById(gamePlatformDTO.getPlatformId());
        if (platformOpt.isPresent()) {
            GamePlatform gamePlatform = convertToEntity(gamePlatformDTO);
            gamePlatform.setPlatform(platformOpt.get());
            GamePlatform savedGamePlatform = gamePlatformRepository.save(gamePlatform);
            return Optional.of(convertToDTO(savedGamePlatform));
        }
        return Optional.empty();
    }

    // Обновление существующей игровой платформы
    public Optional<GamePlatformDTO> updateGamePlatform(Long id, GamePlatformDTO gamePlatformDTO) {
        return gamePlatformRepository.findById(id).map(gamePlatform -> {
            gamePlatform.setName(gamePlatformDTO.getName());
            gamePlatform.setUrlImage(gamePlatformDTO.getUrlImage());
            if (gamePlatformDTO.getPlatformId() != null) {
                Optional<Platform> platformOpt = platformRepository.findById(gamePlatformDTO.getPlatformId());
                platformOpt.ifPresent(gamePlatform::setPlatform);
            }
            // Если необходимо обновить связи с Game, это можно сделать здесь
            GamePlatform updatedGamePlatform = gamePlatformRepository.save(gamePlatform);
            return convertToDTO(updatedGamePlatform);
        });
    }

    // Удаление игровой платформы
    public boolean deleteGamePlatform(Long id) {
        return gamePlatformRepository.findById(id).map(gamePlatform -> {
            gamePlatformRepository.delete(gamePlatform);
            return true;
        }).orElse(false);
    }

    // Конвертация сущности в DTO
    private GamePlatformDTO convertToDTO(GamePlatform gamePlatform) {
        Set<Long> gameIds = gamePlatform.getGames().stream()
                .map(Game::getId)
                .collect(Collectors.toSet());

        return GamePlatformDTO.builder()
                .id(gamePlatform.getId())
                .name(gamePlatform.getName())
                .urlImage(gamePlatform.getUrlImage())
                .platformId(gamePlatform.getPlatform() != null ? gamePlatform.getPlatform().getId() : null)
                .gameIds(gameIds)
                .build();
    }

    // Конвертация DTO в сущность
    private GamePlatform convertToEntity(GamePlatformDTO gamePlatformDTO) {
        GamePlatform gamePlatform = new GamePlatform();
        gamePlatform.setName(gamePlatformDTO.getName());
        gamePlatform.setUrlImage(gamePlatformDTO.getUrlImage());
        // Связь с Platform будет установлена отдельно
        return gamePlatform;
    }
}