package STP.GamePlatform.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String characteristic;

    private String urlImage;

    // Одно Platform может иметь много GamePlatforms
    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<GamePlatform> gamePlatforms = new HashSet<>();
}