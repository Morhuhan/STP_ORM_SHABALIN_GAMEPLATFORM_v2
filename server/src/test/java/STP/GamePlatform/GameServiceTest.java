package STP.GamePlatform;

import STP.GamePlatform.DTO.GameDTO;
import STP.GamePlatform.Entities.Game;
import STP.GamePlatform.Entities.GamePlatform;
import STP.GamePlatform.Reprsitories.GamePlatformRepository;
import STP.GamePlatform.Reprsitories.GameRepository;
import STP.GamePlatform.Services.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePlatformRepository gamePlatformRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private GameDTO gameDTO;
    private GamePlatform gamePlatform;

    @BeforeEach
    public void setUp() {
        gamePlatform = GamePlatform.builder()
                .id(1L)
                .name("Platform1")
                .build();

        game = Game.builder()
                .id(1L)
                .name("Game1")
                .genre("Action")
                .releaseDate("2023-01-01")
                .cover("http://example.com/cover.jpg")
                .score(9.5)
                .gamePlatforms(new HashSet<>(Arrays.asList(gamePlatform)))
                .build();

        gameDTO = GameDTO.builder()
                .id(1L)
                .name("Game1")
                .genre("Action")
                .releaseDate("2023-01-01")
                .cover("http://example.com/cover.jpg")
                .score(9.5)
                .gamePlatformIds(new HashSet<>(Arrays.asList(1L)))
                .build();
    }

    @Test
    public void testGetAllGames() {
        when(gameRepository.findAll()).thenReturn(Arrays.asList(game));

        List<GameDTO> result = gameService.getAllGames();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(gameDTO, result.get(0));
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    public void testGetGameById_Found() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Optional<GameDTO> result = gameService.getGameById(1L);

        assertTrue(result.isPresent());
        assertEquals(gameDTO, result.get());
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetGameById_NotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GameDTO> result = gameService.getGameById(1L);

        assertFalse(result.isPresent());
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateGame() {
        Game gameToSave = Game.builder()
                .name("Game1")
                .genre("Action")
                .releaseDate("2023-01-01")
                .cover("http://example.com/cover.jpg")
                .score(9.5)
                .gamePlatforms(new HashSet<>(Arrays.asList(gamePlatform)))
                .build();

        when(gamePlatformRepository.findAllById(anySet())).thenReturn(Arrays.asList(gamePlatform));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GameDTO result = gameService.createGame(gameDTO);

        assertNotNull(result);
        assertEquals(gameDTO, result);
        verify(gamePlatformRepository, times(1)).findAllById(anySet());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void testUpdateGame_Found() {
        Game updatedGame = Game.builder()
                .id(1L)
                .name("UpdatedGame")
                .genre("Adventure")
                .releaseDate("2023-02-02")
                .cover("http://example.com/updated_cover.jpg")
                .score(8.5)
                .gamePlatforms(new HashSet<>())
                .build();

        GameDTO updatedGameDTO = GameDTO.builder()
                .id(1L)
                .name("UpdatedGame")
                .genre("Adventure")
                .releaseDate("2023-02-02")
                .cover("http://example.com/updated_cover.jpg")
                .score(8.5)
                .gamePlatformIds(new HashSet<>())
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
        when(gameRepository.save(any(Game.class))).thenReturn(updatedGame);

        Optional<GameDTO> result = gameService.updateGame(1L, updatedGameDTO);

        assertTrue(result.isPresent());
        assertEquals(updatedGameDTO, result.get());
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void testUpdateGame_NotFound() {
        GameDTO updatedGameDTO = GameDTO.builder()
                .id(1L)
                .name("UpdatedGame")
                .genre("Adventure")
                .releaseDate("2023-02-02")
                .cover("http://example.com/updated_cover.jpg")
                .score(8.5)
                .gamePlatformIds(new HashSet<>())
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GameDTO> result = gameService.updateGame(1L, updatedGameDTO);

        assertFalse(result.isPresent());
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testDeleteGame_Found() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        doNothing().when(gameRepository).delete(game);

        boolean result = gameService.deleteGame(1L);

        assertTrue(result);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).delete(game);
    }

    @Test
    public void testDeleteGame_NotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = gameService.deleteGame(1L);

        assertFalse(result);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).delete(any(Game.class));
    }

    @Test
    public void testAssignGameToGamePlatform_Success() {
        // Убедимся, что игра изначально не привязана к платформе
        game.setGamePlatforms(new HashSet<>());

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        Optional<GameDTO> result = gameService.assignGameToGamePlatform(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(gameDTO, result.get());
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testAssignGameToGamePlatform_AlreadyAssigned() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));

        Optional<GameDTO> result = gameService.assignGameToGamePlatform(1L, 1L);

        assertFalse(result.isPresent());
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testAssignGameToGamePlatform_GameNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));

        Optional<GameDTO> result = gameService.assignGameToGamePlatform(1L, 1L);

        assertFalse(result.isPresent());
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testAssignGameToGamePlatform_GamePlatformNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GameDTO> result = gameService.assignGameToGamePlatform(1L, 1L);

        assertFalse(result.isPresent());
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testUnassignGameFromGamePlatform_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        boolean result = gameService.unassignGameFromGamePlatform(1L, 1L);

        assertTrue(result);
        assertFalse(game.getGamePlatforms().contains(gamePlatform));
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUnassignGameFromGamePlatform_NotAssigned() {
        game.setGamePlatforms(new HashSet<>());
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));

        boolean result = gameService.unassignGameFromGamePlatform(1L, 1L);

        assertFalse(result);
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testUnassignGameFromGamePlatform_GameNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));

        boolean result = gameService.unassignGameFromGamePlatform(1L, 1L);

        assertFalse(result);
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testUnassignGameFromGamePlatform_GamePlatformNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = gameService.unassignGameFromGamePlatform(1L, 1L);

        assertFalse(result);
        verify(gameRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any(Game.class));
    }
}
