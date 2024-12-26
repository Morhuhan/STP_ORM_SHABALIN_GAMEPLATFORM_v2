package STP.GamePlatform.Reprsitories;

import STP.GamePlatform.Entities.GamePlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePlatformRepository extends JpaRepository<GamePlatform, Long> {
}