package STP.GamePlatform;

import STP.GamePlatform.DTO.GamePlatformDTO;
import STP.GamePlatform.Entities.Game;
import STP.GamePlatform.Entities.GamePlatform;
import STP.GamePlatform.Entities.Platform;
import STP.GamePlatform.Reprsitories.GamePlatformRepository;
import STP.GamePlatform.Reprsitories.PlatformRepository;
import STP.GamePlatform.Services.GamePlatformService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamePlatformServiceTest {

    @Mock
    private GamePlatformRepository gamePlatformRepository;

    @Mock
    private PlatformRepository platformRepository;

    @InjectMocks
    private GamePlatformService gamePlatformService;

    private Platform platform;
    private GamePlatform gamePlatform;
    private GamePlatformDTO gamePlatformDTO;

    @BeforeEach
    public void setUp() {
        platform = Platform.builder()
                .id(1L)
                .name("Platform1")
                .build();

        gamePlatform = GamePlatform.builder()
                .id(1L)
                .name("GamePlatform1")
                .urlImage("http://example.com/image.jpg")
                .platform(platform)
                .games(new HashSet<>())
                .build();

        gamePlatformDTO = GamePlatformDTO.builder()
                .id(1L)
                .name("GamePlatform1")
                .urlImage("http://example.com/image.jpg")
                .platformId(1L)
                .gameIds(new HashSet<>())
                .build();
    }

    @Test
    public void testGetAllGamePlatforms() {
        when(gamePlatformRepository.findAll()).thenReturn(Arrays.asList(gamePlatform));

        List<GamePlatformDTO> result = gamePlatformService.getAllGamePlatforms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(gamePlatformDTO, result.get(0));
        verify(gamePlatformRepository, times(1)).findAll();
    }

    @Test
    public void testGetGamePlatformById_Found() {
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));

        Optional<GamePlatformDTO> result = gamePlatformService.getGamePlatformById(1L);

        assertTrue(result.isPresent());
        assertEquals(gamePlatformDTO, result.get());
        verify(gamePlatformRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetGamePlatformById_NotFound() {
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GamePlatformDTO> result = gamePlatformService.getGamePlatformById(1L);

        assertFalse(result.isPresent());
        verify(gamePlatformRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateGamePlatform_Success() {
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(gamePlatformRepository.save(any(GamePlatform.class))).thenReturn(gamePlatform);

        Optional<GamePlatformDTO> result = gamePlatformService.createGamePlatform(gamePlatformDTO);

        assertTrue(result.isPresent());
        assertEquals(gamePlatformDTO, result.get());
        verify(platformRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).save(any(GamePlatform.class));
    }

    @Test
    public void testCreateGamePlatform_PlatformNotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GamePlatformDTO> result = gamePlatformService.createGamePlatform(gamePlatformDTO);

        assertFalse(result.isPresent());
        verify(platformRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(0)).save(any(GamePlatform.class));
    }

    @Test
    public void testUpdateGamePlatform_Found() {
        GamePlatform updatedGamePlatform = GamePlatform.builder()
                .id(1L)
                .name("UpdatedGamePlatform")
                .urlImage("http://example.com/updated_image.jpg")
                .platform(platform)
                .games(new HashSet<>())
                .build();

        GamePlatformDTO updatedDTO = GamePlatformDTO.builder()
                .id(1L)
                .name("UpdatedGamePlatform")
                .urlImage("http://example.com/updated_image.jpg")
                .platformId(1L)
                .gameIds(new HashSet<>())
                .build();

        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(gamePlatformRepository.save(any(GamePlatform.class))).thenReturn(updatedGamePlatform);

        Optional<GamePlatformDTO> result = gamePlatformService.updateGamePlatform(1L, updatedDTO);

        assertTrue(result.isPresent());
        assertEquals(updatedDTO, result.get());
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(platformRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).save(any(GamePlatform.class));
    }

    @Test
    public void testUpdateGamePlatform_NotFound() {
        GamePlatformDTO updatedDTO = GamePlatformDTO.builder()
                .id(1L)
                .name("UpdatedGamePlatform")
                .urlImage("http://example.com/updated_image.jpg")
                .platformId(1L)
                .gameIds(new HashSet<>())
                .build();

        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<GamePlatformDTO> result = gamePlatformService.updateGamePlatform(1L, updatedDTO);

        assertFalse(result.isPresent());
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(platformRepository, times(0)).findById(anyLong());
        verify(gamePlatformRepository, times(0)).save(any(GamePlatform.class));
    }

    @Test
    public void testDeleteGamePlatform_Found() {
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.of(gamePlatform));
        doNothing().when(gamePlatformRepository).delete(gamePlatform);

        boolean result = gamePlatformService.deleteGamePlatform(1L);

        assertTrue(result);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(1)).delete(gamePlatform);
    }

    @Test
    public void testDeleteGamePlatform_NotFound() {
        when(gamePlatformRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = gamePlatformService.deleteGamePlatform(1L);

        assertFalse(result);
        verify(gamePlatformRepository, times(1)).findById(1L);
        verify(gamePlatformRepository, times(0)).delete(any(GamePlatform.class));
    }

    @Test
    public void testConvertToDTO() {
        Game game = Game.builder()
                .id(1L)
                .name("Game1")
                .genre("Action")
                .releaseDate("2023-01-01")
                .cover("http://example.com/game1.jpg")
                .score(9.0)
                .build();

        gamePlatform.getGames().add(game);

        GamePlatformDTO expectedDTO = GamePlatformDTO.builder()
                .id(1L)
                .name("GamePlatform1")
                .urlImage("http://example.com/image.jpg")
                .platformId(1L)
                .gameIds(new HashSet<>(Arrays.asList(1L)))
                .build();

        GamePlatformDTO actualDTO = invokePrivateConvertToDTO(gamePlatform);

        assertEquals(expectedDTO, actualDTO);
    }

    private GamePlatformDTO invokePrivateConvertToDTO(GamePlatform gamePlatform) {
        try {
            java.lang.reflect.Method method = GamePlatformService.class.getDeclaredMethod("convertToDTO", GamePlatform.class);
            method.setAccessible(true);
            return (GamePlatformDTO) method.invoke(gamePlatformService, gamePlatform);
        } catch (Exception e) {
            fail("Failed to invoke convertToDTO: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testConvertToEntity() {
        GamePlatformDTO dto = GamePlatformDTO.builder()
                .name("NewGamePlatform")
                .urlImage("http://example.com/new_image.jpg")
                .build();

        GamePlatform expectedEntity = GamePlatform.builder()
                .name("NewGamePlatform")
                .urlImage("http://example.com/new_image.jpg")
                .build();

        GamePlatform actualEntity = invokePrivateConvertToEntity(dto);

        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getUrlImage(), actualEntity.getUrlImage());
    }

    private GamePlatform invokePrivateConvertToEntity(GamePlatformDTO dto) {
        try {
            java.lang.reflect.Method method = GamePlatformService.class.getDeclaredMethod("convertToEntity", GamePlatformDTO.class);
            method.setAccessible(true);
            return (GamePlatform) method.invoke(gamePlatformService, dto);
        } catch (Exception e) {
            fail("Failed to invoke convertToEntity: " + e.getMessage());
            return null;
        }
    }
}
