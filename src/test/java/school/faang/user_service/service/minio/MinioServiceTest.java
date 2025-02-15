package school.faang.user_service.service.minio;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.customexception.MinioException;
import school.faang.user_service.properties.MinioProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioServiceTest {
    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private MinioService minioService;

    private String fileName;
    private byte[] data;
    private String contentType;

    @BeforeEach
    public void setup() {
        fileName = "test_file.jpeg";
        data = new byte[66];
        contentType = "image/jpeg";

        when(minioProperties.getBucketName()).thenReturn("corpbucket");
    }

    @Test
    public void uploadFileTest() throws Exception {
        minioService.uploadFile(1L, fileName, data, contentType);
        verify(minioClient, times(1))
                .putObject(any(PutObjectArgs.class));
    }

    @Test
    public void uploadFileErrorTest() throws Exception {
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException());

        assertThrows(MinioException.class, () ->
                minioService.uploadFile(1L, fileName, data, contentType)
        );
    }

    @Test
    public void downloadFileTest() throws Exception {
        byte[] expectedData = new byte[66];
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
        when(mockResponse.readAllBytes()).thenReturn(expectedData);

        byte[] result = minioService.downloadFile(fileName);

        Assertions.assertArrayEquals(expectedData, result);
        verify(minioClient, times(1))
                .getObject(any(GetObjectArgs.class));
    }

    @Test
    public void downloadFileErrorTest() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException());

        assertThrows(MinioException.class, () ->
                minioService.downloadFile(fileName)
        );
    }

    @Test
    public void deleteFileTest() throws Exception {
        minioService.deleteFile(fileName);
        verify(minioClient, times(1))
                .removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    public void deleteFileErrorTest() throws Exception {
        doThrow(new RuntimeException()).when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        assertThrows(MinioException.class, () ->
                minioService.deleteFile(fileName)
        );
    }
}
