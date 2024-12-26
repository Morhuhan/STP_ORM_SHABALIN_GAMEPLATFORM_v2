package STP.GamePlatform.Services;

import STP.GamePlatform.DTO.PlatformDTO;
import STP.GamePlatform.Entities.Platform;
import STP.GamePlatform.Reprsitories.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    // Получение всех платформ
    public List<PlatformDTO> getAllPlatforms() {
        return platformRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение платформы по ID
    public Optional<PlatformDTO> getPlatformById(Long id) {
        return platformRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Создание новой платформы
    public PlatformDTO createPlatform(PlatformDTO platformDTO) {
        Platform platform = convertToEntity(platformDTO);
        Platform savedPlatform = platformRepository.save(platform);
        return convertToDTO(savedPlatform);
    }

    // Обновление существующей платформы
    public Optional<PlatformDTO> updatePlatform(Long id, PlatformDTO platformDTO) {
        return platformRepository.findById(id).map(platform -> {
            platform.setName(platformDTO.getName());
            platform.setCharacteristic(platformDTO.getCharacteristic());
            platform.setUrlImage(platformDTO.getUrlImage());
            // Если необходимо обновить связи с GamePlatform, это можно сделать здесь
            Platform updatedPlatform = platformRepository.save(platform);
            return convertToDTO(updatedPlatform);
        });
    }

    // Удаление платформы
    public boolean deletePlatform(Long id) {
        return platformRepository.findById(id).map(platform -> {
            platformRepository.delete(platform);
            return true;
        }).orElse(false);
    }

    // Конвертация сущности в DTO
    private PlatformDTO convertToDTO(Platform platform) {
        Set<Long> gamePlatformIds = platform.getGamePlatforms().stream()
                .map(gp -> gp.getId())
                .collect(Collectors.toSet());

        return PlatformDTO.builder()
                .id(platform.getId())
                .name(platform.getName())
                .characteristic(platform.getCharacteristic())
                .urlImage(platform.getUrlImage())
                .gamePlatformIds(gamePlatformIds)
                .build();
    }

    // Конвертация DTO в сущность
    private Platform convertToEntity(PlatformDTO platformDTO) {
        Platform platform = new Platform();
        platform.setName(platformDTO.getName());
        platform.setCharacteristic(platformDTO.getCharacteristic());
        platform.setUrlImage(platformDTO.getUrlImage());
        // Связи с GamePlatform будут управляться отдельно
        return platform;
    }
}