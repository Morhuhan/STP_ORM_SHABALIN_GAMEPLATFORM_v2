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
public class GamePlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String urlImage;

    // Много GamePlatforms принадлежат одному Platform
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    @ToString.Exclude
    private Platform platform;

    // Связь с Game через ManyToMany
    @ManyToMany(mappedBy = "gamePlatforms")
    @ToString.Exclude
    private Set<Game> games = new HashSet<>();
}