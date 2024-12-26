package STP.GamePlatform.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePlatformAssignmentDTO {
    private Long gameId;
    private Long gamePlatformId;
}