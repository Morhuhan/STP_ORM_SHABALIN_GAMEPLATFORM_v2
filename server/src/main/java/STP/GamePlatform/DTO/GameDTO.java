package STP.GamePlatform.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO {

    private Long id;

    @NotBlank(message = "Название игры не может быть пустым.")
    @Size(max = 100, message = "Название игры не может превышать 100 символов.")
    private String name;

    @NotBlank(message = "Жанр игры не может быть пустым.")
    @Size(max = 50, message = "Жанр игры не может превышать 50 символов.")
    private String genre;

    @NotBlank(message = "Дата релиза не может быть пустой.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата релиза должна быть в формате ГГГГ-ММ-ДД.")
    private String releaseDate;

    @NotBlank(message = "Ссылка на обложку не может быть пустой.")
    @URL(message = "Ссылка на обложку должна быть валидным URL.")
    private String cover;

    @NotNull(message = "Оценка не может быть пустой.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Оценка должна быть не меньше 0.")
    @DecimalMax(value = "10.0", inclusive = true, message = "Оценка должна быть не больше 10.")
    private Double score;

    private Set<Long> gamePlatformIds; // IDs связанных GamePlatform
}