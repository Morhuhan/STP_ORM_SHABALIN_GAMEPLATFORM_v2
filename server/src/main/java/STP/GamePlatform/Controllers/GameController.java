package STP.GamePlatform.Controllers;

import STP.GamePlatform.DTO.ApiResponse;
import STP.GamePlatform.DTO.GameDTO;
import STP.GamePlatform.DTO.GamePlatformAssignmentDTO;
import STP.GamePlatform.Services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // Получение всех игр
    @GetMapping
    public ResponseEntity<ApiResponse<List<GameDTO>>> getAllGames() {
        List<GameDTO> games = gameService.getAllGames();
        ApiResponse<List<GameDTO>> response = ApiResponse.<List<GameDTO>>builder()
                .success(true)
                .data(games)
                .build();
        return ResponseEntity.ok(response);
    }

    // Получение игры по ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameDTO>> getGameById(@PathVariable Long id) {
        Optional<GameDTO> gameDTO = gameService.getGameById(id);
        if (gameDTO.isPresent()) {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(true)
                    .data(gameDTO.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(false)
                    .message("Игра не найдена.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Создание новой игры
    @PostMapping
    public ResponseEntity<ApiResponse<GameDTO>> createGame(@Valid @RequestBody GameDTO gameDTO) {
        GameDTO createdGame = gameService.createGame(gameDTO);
        ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                .success(true)
                .data(createdGame)
                .message("Игра успешно создана.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Обновление существующей игры
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GameDTO>> updateGame(@PathVariable Long id, @Valid @RequestBody GameDTO gameDTO) {
        Optional<GameDTO> updatedGame = gameService.updateGame(id, gameDTO);
        if (updatedGame.isPresent()) {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(true)
                    .data(updatedGame.get())
                    .message("Игра успешно обновлена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(false)
                    .message("Не удалось обновить игру. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Удаление игры
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGame(@PathVariable Long id) {
        boolean deleted = gameService.deleteGame(id);
        if (deleted) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Игра успешно удалена.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Не удалось удалить игру. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Привязать игру к игровой платформе
    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<GameDTO>> assignGameToGamePlatform(
            @PathVariable Long id,
            @Valid @RequestBody GamePlatformAssignmentDTO assignmentDTO) {

        // Используем только gamePlatformId из тела запроса
        Optional<GameDTO> updatedGame = gameService.assignGameToGamePlatform(id, assignmentDTO.getGamePlatformId());

        if (updatedGame.isPresent()) {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(true)
                    .data(updatedGame.get())
                    .message("Игра успешно привязана к игровой платформе.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<GameDTO> response = ApiResponse.<GameDTO>builder()
                    .success(false)
                    .message("Не удалось привязать игру к игровой платформе. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Отвязать игру от игровой платформы
    @PostMapping("/{id}/unassign")
    public ResponseEntity<ApiResponse<Void>> unassignGameFromGamePlatform(
            @PathVariable Long id,
            @Valid @RequestBody GamePlatformAssignmentDTO assignmentDTO) {

        // Используем только gamePlatformId из тела запроса
        boolean success = gameService.unassignGameFromGamePlatform(id, assignmentDTO.getGamePlatformId());
        if (success) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Игра успешно отвязана от игровой платформы.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Не удалось отвязать игру от игровой платформы. Проверьте ID.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}