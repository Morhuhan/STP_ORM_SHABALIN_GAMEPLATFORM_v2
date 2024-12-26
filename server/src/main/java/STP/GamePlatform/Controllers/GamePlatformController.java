package STP.GamePlatform.Controllers;

import STP.GamePlatform.DTO.ApiResponse;
import STP.GamePlatform.DTO.GamePlatformDTO;
import STP.GamePlatform.Services.GamePlatformService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gameplatforms")
public class GamePlatformController {

    @Autowired
    private GamePlatformService gamePlatformService;

    // Получение всех игровых платформ
    @GetMapping
    public ResponseEntity<ApiResponse<List<GamePlatformDTO>>> getAllGamePlatforms() {
        List<GamePlatformDTO> gamePlatforms = gamePlatformService.getAllGamePlatforms();
        ApiResponse<List<GamePlatformDTO>> response = ApiResponse.<List<GamePlatformDTO>>builder()
                .success(true)
                .data(gamePlatforms)
                .build();
        return ResponseEntity.ok(response);
    }

    // Получение игровой платформы по ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GamePlatformDTO>> getGamePlatformById(@PathVariable Long id) {
        Optional<GamePlatformDTO> gamePlatformDTO = gamePlatformService.getGamePlatformById(id);
        if (gamePlatformDTO.isPresent()) {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(true)
                    .data(gamePlatformDTO.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(false)
                    .message("Игровая платформа не найдена.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Создание новой игровой платформы
    @PostMapping
    public ResponseEntity<ApiResponse<GamePlatformDTO>> createGamePlatform(@Valid @RequestBody GamePlatformDTO gamePlatformDTO) {
        Optional<GamePlatformDTO> createdGamePlatform = gamePlatformService.createGamePlatform(gamePlatformDTO);
        if (createdGamePlatform.isPresent()) {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(true)
                    .data(createdGamePlatform.get())
                    .message("Игровая платформа успешно создана.")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(false)
                    .message("Не удалось создать игровую платформу. Проверьте Platform ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Обновление существующей игровой платформы
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GamePlatformDTO>> updateGamePlatform(@PathVariable Long id, @Valid @RequestBody GamePlatformDTO gamePlatformDTO) {
        Optional<GamePlatformDTO> updatedGamePlatform = gamePlatformService.updateGamePlatform(id, gamePlatformDTO);
        if (updatedGamePlatform.isPresent()) {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(true)
                    .data(updatedGamePlatform.get())
                    .message("Игровая платформа успешно обновлена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<GamePlatformDTO> response = ApiResponse.<GamePlatformDTO>builder()
                    .success(false)
                    .message("Не удалось обновить игровую платформу. Проверьте ID и Platform ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Удаление игровой платформы
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGamePlatform(@PathVariable Long id) {
        boolean deleted = gamePlatformService.deleteGamePlatform(id);
        if (deleted) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Игровая платформа успешно удалена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Не удалось удалить игровую платформу. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}