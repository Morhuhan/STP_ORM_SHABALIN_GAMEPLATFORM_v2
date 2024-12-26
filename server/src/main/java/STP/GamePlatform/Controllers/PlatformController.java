package STP.GamePlatform.Controllers;

import STP.GamePlatform.DTO.ApiResponse;
import STP.GamePlatform.DTO.PlatformDTO;
import STP.GamePlatform.Services.PlatformService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    // Получение всех платформ
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlatformDTO>>> getAllPlatforms() {
        List<PlatformDTO> platforms = platformService.getAllPlatforms();
        ApiResponse<List<PlatformDTO>> response = ApiResponse.<List<PlatformDTO>>builder()
                .success(true)
                .data(platforms)
                .build();
        return ResponseEntity.ok(response);
    }

    // Получение платформы по ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlatformDTO>> getPlatformById(@PathVariable Long id) {
        Optional<PlatformDTO> platformDTO = platformService.getPlatformById(id);
        if (platformDTO.isPresent()) {
            ApiResponse<PlatformDTO> response = ApiResponse.<PlatformDTO>builder()
                    .success(true)
                    .data(platformDTO.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<PlatformDTO> response = ApiResponse.<PlatformDTO>builder()
                    .success(false)
                    .message("Платформа не найдена.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Создание новой платформы
    @PostMapping
    public ResponseEntity<ApiResponse<PlatformDTO>> createPlatform(@Valid @RequestBody PlatformDTO platformDTO) {
        PlatformDTO createdPlatform = platformService.createPlatform(platformDTO);
        ApiResponse<PlatformDTO> response = ApiResponse.<PlatformDTO>builder()
                .success(true)
                .data(createdPlatform)
                .message("Платформа успешно создана.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Обновление существующей платформы
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlatformDTO>> updatePlatform(@PathVariable Long id, @Valid @RequestBody PlatformDTO platformDTO) {
        Optional<PlatformDTO> updatedPlatform = platformService.updatePlatform(id, platformDTO);
        if (updatedPlatform.isPresent()) {
            ApiResponse<PlatformDTO> response = ApiResponse.<PlatformDTO>builder()
                    .success(true)
                    .data(updatedPlatform.get())
                    .message("Платформа успешно обновлена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<PlatformDTO> response = ApiResponse.<PlatformDTO>builder()
                    .success(false)
                    .message("Не удалось обновить платформу. Проверьте ID и Platform ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Удаление платформы
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlatform(@PathVariable Long id) {
        boolean deleted = platformService.deletePlatform(id);
        if (deleted) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Платформа успешно удалена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Не удалось удалить платформу. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}