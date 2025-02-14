package school.faang.user_service.service.avatar;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.exception.customexception.DiceBearException;
import school.faang.user_service.properties.DiceBearProperties;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    private static final Long USER_ID = 666L;

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private WebClient webClient;

    private MockWebServer mockWebServer;
    private DiceBearProperties diceBearProperties;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        diceBearProperties = new DiceBearProperties();
        avatarService = new AvatarService(webClient, diceBearProperties);
    }

    @AfterEach
    void shutdownMockServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getDiceBearAvatarSuccessTest() {
        byte[] avatarData = "random_avatar".getBytes();
        mockWebServer.enqueue(new MockResponse()
                .setBody(new String(avatarData))
                .addHeader("Content-Type", "image/png")
                .setResponseCode(200));

        Optional<byte[]> result = avatarService.getRandomDiceBearAvatar(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(avatarData.length, result.get().length);
    }

//    @Test
//    void getDiceBearAvatarEmptyContentTest() {
//        mockWebServer.enqueue(new MockResponse()
//                .setBody("")
//                .addHeader("Content-Type", "image/png")
//                .setResponseCode(200));
//
//        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
//            avatarService.getRandomDiceBearAvatar(USER_ID).orElseThrow(() ->
//                    new DiceBearException());
//        });
//
//        assertEquals();
//    }
//
//    @Test
//    void getDiceBearAvatarServerErrorTest() {
//        mockWebServer.enqueue(new MockResponse().setResponseCode(400));
//
//        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
//            avatarService.getRandomDiceBearAvatar(USER_ID).orElseThrow(() ->
//                    new DiceBearException();
//        });
//
//        assertTrue(exception.getMessage().contains("Error retrieving avatar"));
//    }
//
//    @Test
//    void getDiceBearAvatarServiceUnavailableTest() throws Exception {
//        mockWebServer.shutdown();
//
//        DiceBearException exception = assertThrows(DiceBearException.class, () -> {
//            avatarService.getRandomDiceBearAvatar(USER_ID);
//        });
//
//        assertEquals();
//    }
}
