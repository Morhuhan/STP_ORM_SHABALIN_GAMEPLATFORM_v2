package STP.GamePlatform.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePlatformDTO {

    private Long id;

    @NotBlank(message = "Название GamePlatform не может быть пустым.")
    @Size(max = 100, message = "Название GamePlatform не может превышать 100 символов.")
    private String name;

    @NotBlank(message = "Ссылка на изображение не может быть пустой.")
    @URL(message = "Ссылка на изображение должна быть валидным URL.")
    private String urlImage;

    @NotNull(message = "ID платформы не может быть null.")
    private Long platformId; // ID связанной Platform

    private Set<@NotNull(message = "ID игры не может быть null.") Long> gameIds; // ID связанных Game
}