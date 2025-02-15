package school.faang.user_service.controller.avatar;

import com.redis.testcontainers.RedisContainer;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AvatarControllerIT {

    @Autowired
    private AvatarController avatarController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MinioClient minioClient;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Container
    public static final GenericContainer<?> MINIO_CONTAINER = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "user")
            .withEnv("MINIO_ROOT_PASSWORD", "password")
            .withCommand("server /data");


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.driver-class-name", POSTGRESQL_CONTAINER::getDriverClassName);
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

        registry.add("minio.endpoint", () -> "http://" + MINIO_CONTAINER.getHost() + ":" + MINIO_CONTAINER.getMappedPort(9000));
        registry.add("minio.accessKey", () -> "user");
        registry.add("minio.secretKey", () -> "password");
        registry.add("minio.bucketName", () -> "corpbucket");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void setupMinio() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://" + MINIO_CONTAINER.getHost() + ":" + MINIO_CONTAINER.getMappedPort(9000))
                .credentials("user", "password")
                .build();

        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("corpbucket").build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("corpbucket").build());
        }
    }

    @Test
    void testUploadUserAvatar() throws Exception {
        // Загружаем настоящий файл из ресурсов
        File file = new File("src/test/resources/files/test-image.png");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/png",
                new FileInputStream(file)
        );

        // Отправляем запрос на загрузку файла
        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/2/avatar")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").exists())
                .andExpect(jsonPath("$.smallFileId").exists());

        // Проверяем, что файл действительно загружен в MinIO
        boolean found = minioClient.statObject(StatObjectArgs.builder()
                .bucket("corpbucket")
                .object("2.png")
                .build()) != null;

        Assertions.assertTrue(found, "Файл должен быть загружен в MinIO");
    }

    @Test
    void testGenerateUserAvatarWhenFileIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/7/avatar")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").exists())
                .andExpect(jsonPath("$.smallFileId").exists());

        boolean found = minioClient.statObject(StatObjectArgs.builder()
                .bucket("corpbucket")
                .object("7.png")
                .build()) != null;

        Assertions.assertTrue(found, "Файл должен быть загружен в MinIO");
    }

    @Test
    void testGetUserAvatar() throws Exception {
        // Загружаем настоящий файл из ресурсов
        File file = new File("src/test/resources/files/test-image.png");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/png",
                new FileInputStream(file)
        );

        // Отправляем запрос на загрузку файла
        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/4/avatar")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        // Скачиваем файл из MinIO
        InputStream downloadedFile = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("corpbucket")
                        .object("4.png")
                        .build()
        );

        // Проверяем, что файл можно скачать
        Assertions.assertNotNull(downloadedFile, "Файл должен быть доступен для скачивания");
    }

    @Test
    void testDeleteUserAvatar() throws Exception {
        File file = new File("src/test/resources/files/test-image.png");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/png",
                new FileInputStream(file)
        );

        // Отправляем запрос на загрузку файла
        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/5/avatar")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        // Удаляем файл из MinIO
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket("corpbucket")
                        .object("5.png")
                        .build()
        );

        // Проверяем, что файл действительно удален
        Assertions.assertThrows(Exception.class, () -> {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket("corpbucket")
                    .object("1.png")
                    .build());
        }, "Файл не должен существовать после удаления");
    }
}
