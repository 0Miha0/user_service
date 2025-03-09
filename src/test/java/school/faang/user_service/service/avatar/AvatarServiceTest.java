package school.faang.user_service.service.avatar;

import jakarta.persistence.EntityNotFoundException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.customexception.AvatarProcessingException;
import school.faang.user_service.exception.customexception.MinioException;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.dicebear.DiceBearService;
import school.faang.user_service.service.minio.MinioService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.AvatarValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
//class AvatarServiceTest {
//
//    private static final Long USER_ID = 666L;
//
//    @InjectMocks
//    private AvatarService avatarService;
//
//    @Mock
//    private MinioService minioService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private DiceBearService diceBearService;
//
//    @Mock
//    private AvatarValidator avatarValidator;
//
//    @Spy
//    private UserProfilePicMapper userProfilePicMapper;
//
//    private MockWebServer mockWebServer;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//        avatarService = new AvatarService(
//                minioService,
//                userRepository,
//                userService,
//                diceBearService,
//                userProfilePicMapper,
//                avatarValidator);
//    }
//
//    @AfterEach
//    void shutdownMockServer() throws Exception {
//        mockWebServer.shutdown();
//    }
//
//    @Test
//    void getUserAvatarSuccessTest() {
//        byte[] avatarData = "user_avatar_data".getBytes();
//        UserProfilePic userProfilePic = new UserProfilePic("fileId", "smallFileId");
//
//        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.of(userProfilePic));
//        when(minioService.downloadFile("fileId")).thenReturn(avatarData);
//
//        byte[] result = avatarService.getUserAvatar(USER_ID);
//
//        assertNotNull(result);
//        assertEquals(avatarData.length, result.length);
//    }
//
//    @Test
//    void getUserAvatarNotFoundTest() {
//        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//            avatarService.getUserAvatar(USER_ID);
//        });
//
//        assertEquals("Avatar not found for user ID: " + USER_ID, exception.getMessage());
//    }
//
//    @Test
//    void uploadUserAvatarSuccessTest() throws IOException {
//        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
//        byte[] fileData = Files.readAllBytes(imagePath);
//
//        MultipartFile mockFile = mock(MultipartFile.class);
//        when(mockFile.getBytes()).thenReturn(fileData);
//        when(mockFile.getContentType()).thenReturn("image/png");
//
//        User user = new User();
//        user.setUserProfilePic(new UserProfilePic(null, null));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        doNothing().when(minioService).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/png"));
//
//        UserProfilePicDto result = avatarService.uploadUserAvatar(USER_ID, mockFile);
//
////        assertNotNull(result);
////        verify(minioService, times(2)).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/jpeg"));
////        verify(userRepository).updateProfilePic(eq(USER_ID), anyString(), anyString());
//    }
//
//    @Test
//    void uploadUserAvatarCheckExistsTest() {
//        User user = new User();
//        user.setUserProfilePic(new UserProfilePic("fileId", "smallFileId"));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        AvatarProcessingException exception = assertThrows(AvatarProcessingException.class, () -> {
//            avatarService.uploadUserAvatar(USER_ID, null);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to generate avatar"));
//    }
//
//    @Test
//    void uploadUserAvatarWithoutFileTest() throws IOException {
//        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
//        byte[] fileData = Files.readAllBytes(imagePath);
//
//        when(diceBearService.getRandomAvatar(USER_ID)).thenReturn(Optional.of(fileData));
//        User user = new User();
//        user.setUserProfilePic(new UserProfilePic(null, null));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        UserProfilePicDto result = avatarService.uploadUserAvatar(USER_ID, null);

//        assertNotNull(result);
//        verify(diceBearService).getRandomAvatar(USER_ID);
//        verify(minioService, times(2)).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/jpeg"));
//        verify(userRepository).updateProfilePic(eq(USER_ID), anyString(), anyString());
//    }
//
//    @Test
//    void deleteUserAvatarSuccessTest() {
//        UserProfilePic userProfilePic = new UserProfilePic("fileId", "smallFileId");
//        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.of(userProfilePic));
//
//        avatarService.deleteUserAvatar(USER_ID);
//
//        verify(minioService).deleteFile("fileId");
//        verify(minioService).deleteFile("smallFileId");
//        verify(userRepository).deleteProfilePic(USER_ID);
//    }
//
//    @Test
//    void deleteUserAvatarNotFoundTest() {
//        when(userRepository.findUserProfilePicByUserId(USER_ID)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//            avatarService.deleteUserAvatar(USER_ID);
//        });
//
//        assertEquals("Error generating avatar using DiceBear service.", exception.getMessage());
//    }
//
//    @Test
//    void uploadUserAvatarResizeImageExceptionTest() throws IOException {
//        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
//        byte[] fileData = Files.readAllBytes(imagePath);
//
//        MultipartFile mockFile = mock(MultipartFile.class);
//        when(mockFile.getBytes()).thenReturn(fileData);
//        when(mockFile.getContentType()).thenReturn("image/png");
//
//        AvatarService spyService = spy(avatarService);
//        doThrow(new IOException()).when(spyService).resizeImage(any(byte[].class), anyInt());
//
//        User user = new User();
//        user.setUserProfilePic(new UserProfilePic(null, null));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            spyService.uploadUserAvatar(USER_ID, mockFile);
//        });
//
//        assertEquals("Error processing avatar", exception.getMessage());
//    }
//
//    @Test
//    void uploadUserAvatarWithNoFileTest() throws IOException {
//        DiceBearService spyDiceBearService = spy(diceBearService);
//        avatarService = new AvatarService(
//                minioService,
//                userRepository,
//                userService,
//                spyDiceBearService,
//                userProfilePicMapper,
//                avatarValidator);
//        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
//        byte[] fileData = Files.readAllBytes(imagePath);
//
//        doReturn(Optional.of(fileData)).when(spyDiceBearService).getRandomAvatar(USER_ID);
//
//        User user = new User();
//        user.setId(USER_ID);
//        user.setUserProfilePic(new UserProfilePic(null, null));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        doNothing().when(minioService).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/png"));
//
//        UserProfilePicDto result = avatarService.uploadUserAvatar(USER_ID, null);

//        assertNotNull(result);
//        verify(minioService, times(2)).uploadFile(anyLong(), anyString(), any(byte[].class), eq("image/jpeg"));
//        verify(userRepository).updateProfilePic(eq(USER_ID), anyString(), anyString());
//    }
//
//    @Test
//    void uploadUserAvatarIOExceptionWhileReadingFileTest() throws IOException {
//        MultipartFile mockFile = mock(MultipartFile.class);
//        when(mockFile.getBytes()).thenThrow(new IOException());
//
//        User user = new User();
//        user.setUserProfilePic(new UserProfilePic(null, null));
//        when(userService.findById(USER_ID)).thenReturn(user);
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            avatarService.uploadUserAvatar(USER_ID, mockFile);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to read the uploaded file"));
//    }
//}
