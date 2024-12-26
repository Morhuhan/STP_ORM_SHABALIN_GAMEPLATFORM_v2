package STP.GamePlatform.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformDTO {

    private Long id;

    @NotBlank(message = "Название платформы не может быть пустым.")
    @Size(max = 100, message = "Название платформы не может превышать 100 символов.")
    private String name;

    @NotBlank(message = "Характеристика платформы не может быть пустой.")
    @Size(max = 500, message = "Характеристика платформы не может превышать 500 символов.")
    private String characteristic;

    @NotBlank(message = "Ссылка на изображение не может быть пустой.")
    @URL(message = "Ссылка на изображение должна быть валидным URL.")
    private String urlImage;

    private Set<@NotNull(message = "ID GamePlatform не может быть null.") Long> gamePlatformIds; // ID связанных GamePlatform
}