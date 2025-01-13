package STP.GamePlatform;

import STP.GamePlatform.DTO.PlatformDTO;
import STP.GamePlatform.Entities.Platform;
import STP.GamePlatform.Reprsitories.PlatformRepository;
import STP.GamePlatform.Services.PlatformService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @InjectMocks
    private PlatformService platformService;

    private Platform platform;
    private PlatformDTO platformDTO;

    @BeforeEach
    public void setUp() {
        platform = Platform.builder()
                .id(1L)
                .name("Platform1")
                .characteristic("High performance gaming platform.")
                .urlImage("http://example.com/platform1.jpg")
                .gamePlatforms(new HashSet<>())
                .build();

        platformDTO = PlatformDTO.builder()
                .id(1L)
                .name("Platform1")
                .characteristic("High performance gaming platform.")
                .urlImage("http://example.com/platform1.jpg")
                .gamePlatformIds(new HashSet<>())
                .build();
    }

    @Test
    public void testGetAllPlatforms() {
        when(platformRepository.findAll()).thenReturn(Arrays.asList(platform));

        List<PlatformDTO> result = platformService.getAllPlatforms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(platformDTO, result.get(0));
        verify(platformRepository, times(1)).findAll();
    }

    @Test
    public void testGetPlatformById_Found() {
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        Optional<PlatformDTO> result = platformService.getPlatformById(1L);

        assertTrue(result.isPresent());
        assertEquals(platformDTO, result.get());
        verify(platformRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPlatformById_NotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PlatformDTO> result = platformService.getPlatformById(1L);

        assertFalse(result.isPresent());
        verify(platformRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreatePlatform_Success() {
        Platform platformToSave = Platform.builder()
                .name("Platform1")
                .characteristic("High performance gaming platform.")
                .urlImage("http://example.com/platform1.jpg")
                .build();

        when(platformRepository.save(any(Platform.class))).thenReturn(platform);

        PlatformDTO result = platformService.createPlatform(platformDTO);

        assertNotNull(result);
        assertEquals(platformDTO, result);
        verify(platformRepository, times(1)).save(any(Platform.class));
    }

    @Test
    public void testUpdatePlatform_Found() {
        Platform updatedPlatform = Platform.builder()
                .id(1L)
                .name("UpdatedPlatform")
                .characteristic("Updated characteristic.")
                .urlImage("http://example.com/updated_platform.jpg")
                .gamePlatforms(new HashSet<>())
                .build();

        PlatformDTO updatedDTO = PlatformDTO.builder()
                .id(1L)
                .name("UpdatedPlatform")
                .characteristic("Updated characteristic.")
                .urlImage("http://example.com/updated_platform.jpg")
                .gamePlatformIds(new HashSet<>())
                .build();

        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        Optional<PlatformDTO> result = platformService.updatePlatform(1L, updatedDTO);

        assertTrue(result.isPresent());
        assertEquals(updatedDTO, result.get());
        verify(platformRepository, times(1)).findById(1L);
        verify(platformRepository, times(1)).save(any(Platform.class));
    }

    @Test
    public void testUpdatePlatform_NotFound() {
        PlatformDTO updatedDTO = PlatformDTO.builder()
                .id(1L)
                .name("UpdatedPlatform")
                .characteristic("Updated characteristic.")
                .urlImage("http://example.com/updated_platform.jpg")
                .gamePlatformIds(new HashSet<>())
                .build();

        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PlatformDTO> result = platformService.updatePlatform(1L, updatedDTO);

        assertFalse(result.isPresent());
        verify(platformRepository, times(1)).findById(1L);
        verify(platformRepository, times(0)).save(any(Platform.class));
    }

    @Test
    public void testDeletePlatform_Found() {
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        doNothing().when(platformRepository).delete(platform);

        boolean result = platformService.deletePlatform(1L);

        assertTrue(result);
        verify(platformRepository, times(1)).findById(1L);
        verify(platformRepository, times(1)).delete(platform);
    }

    @Test
    public void testDeletePlatform_NotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = platformService.deletePlatform(1L);

        assertFalse(result);
        verify(platformRepository, times(1)).findById(1L);
        verify(platformRepository, times(0)).delete(any(Platform.class));
    }

    @Test
    public void testConvertToDTO() {
        Platform platformWithGamePlatforms = Platform.builder()
                .id(1L)
                .name("Platform1")
                .characteristic("High performance gaming platform.")
                .urlImage("http://example.com/platform1.jpg")
                .gamePlatforms(new HashSet<>(Arrays.asList(
                        STP.GamePlatform.Entities.GamePlatform.builder().id(1L).build(),
                        STP.GamePlatform.Entities.GamePlatform.builder().id(2L).build()
                )))
                .build();

        PlatformDTO expectedDTO = PlatformDTO.builder()
                .id(1L)
                .name("Platform1")
                .characteristic("High performance gaming platform.")
                .urlImage("http://example.com/platform1.jpg")
                .gamePlatformIds(new HashSet<>(Arrays.asList(1L, 2L)))
                .build();

        PlatformDTO actualDTO = invokePrivateConvertToDTO(platformWithGamePlatforms);

        assertEquals(expectedDTO, actualDTO);
    }

    private PlatformDTO invokePrivateConvertToDTO(Platform platform) {
        try {
            java.lang.reflect.Method method = PlatformService.class.getDeclaredMethod("convertToDTO", Platform.class);
            method.setAccessible(true);
            return (PlatformDTO) method.invoke(platformService, platform);
        } catch (Exception e) {
            fail("Failed to invoke convertToDTO: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testConvertToEntity() {
        PlatformDTO dto = PlatformDTO.builder()
                .name("NewPlatform")
                .characteristic("New platform characteristic.")
                .urlImage("http://example.com/new_platform.jpg")
                .build();

        Platform expectedEntity = Platform.builder()
                .name("NewPlatform")
                .characteristic("New platform characteristic.")
                .urlImage("http://example.com/new_platform.jpg")
                .build();

        Platform actualEntity = invokePrivateConvertToEntity(dto);

        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getCharacteristic(), actualEntity.getCharacteristic());
        assertEquals(expectedEntity.getUrlImage(), actualEntity.getUrlImage());
    }

    private Platform invokePrivateConvertToEntity(PlatformDTO dto) {
        try {
            java.lang.reflect.Method method = PlatformService.class.getDeclaredMethod("convertToEntity", PlatformDTO.class);
            method.setAccessible(true);
            return (Platform) method.invoke(platformService, dto);
        } catch (Exception e) {
            fail("Failed to invoke convertToEntity: " + e.getMessage());
            return null;
        }
    }
}
