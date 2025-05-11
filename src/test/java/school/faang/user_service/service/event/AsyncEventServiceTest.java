package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncEventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private AsyncEventService asyncEventService;

    @Test
    public void deleteBatchAsyncSuccessTest() throws Exception {
        List<Long> batch = List.of(1L, 2L, 3L);
        asyncEventService.deleteBatchAsync(batch).get();
        verify(eventRepository).deleteAllById(batch);
    }

    @Test
    public void deleteBatchAsyncFailureTest() {
        List<Long> batch = List.of(1L, 2L, 3L);

        doThrow(new RuntimeException())
                .when(eventRepository).deleteAllById(batch);

        assertThrows(
                RuntimeException.class,
                () -> asyncEventService.deleteBatchAsync(batch).get()
        );
        verify(eventRepository).deleteAllById(batch);
    }
}