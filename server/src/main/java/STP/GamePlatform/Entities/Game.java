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
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String genre;

    private String releaseDate;

    private String cover;

    private Double score;

    // Связь с GamePlatform через ManyToMany
    @ManyToMany
    @JoinTable(
            name = "game_gameplatform",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "game_platform_id")
    )
    @ToString.Exclude
    private Set<GamePlatform> gamePlatforms = new HashSet<>();
}