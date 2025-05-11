package school.faang.user_service.service.avatar;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.exception.DiceBearException;
import school.faang.user_service.properties.DiceBearProperties;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiceBearServiceTest {
    private static final Long USER_ID = 666L;

    private DiceBearService diceBearService;

    private MockWebServer mockWebServer;

    @Mock
    private DiceBearProperties diceBearProperties;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        diceBearService = new DiceBearService(webClient, diceBearProperties);

        when(diceBearProperties.getStyle()).thenReturn("bottts");
        when(diceBearProperties.getFormat()).thenReturn("jpeg");
        when(diceBearProperties.getVersion()).thenReturn("v2");
    }

    @AfterEach
    void shutdownMockServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getRandomAvatarSuccessTest() {
        byte[] avatarData = "random_avatar".getBytes();
        mockWebServer.enqueue(new MockResponse()
                .setBody(new String(avatarData))
                .addHeader("Content-Type", "image/jpeg")
                .setResponseCode(200));

        Optional<byte[]> result = diceBearService.getRandomAvatar(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(avatarData.length, result.get().length);
    }

    @Test
    void getRandomAvatarEmptyResponseTest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("")
                .addHeader("Content-Type", "image/jpeg")
                .setResponseCode(200));

        Optional<byte[]> result = diceBearService.getRandomAvatar(USER_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getRandomDiceBearAvatarServerErrorTest() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(DiceBearException.class, () -> {
            diceBearService.getRandomAvatar(USER_ID);
        });
    }
}
