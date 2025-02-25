package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;

    @Value("${app.user-service.premium-service.butch}")
    private int batchSize;


    @Scheduled(cron = "${scheduled.remove-premium.cron}")
    public void removePremium() {
        List<Premium> premiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        List<List<Premium>> batches = splitList(premiums, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(this::deleteBatchAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Async("premiumUserDeletionTaskExecutor")
    public CompletableFuture<Void> deleteBatchAsync(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
        return CompletableFuture.completedFuture(null);
    }

    private List<List<Premium>> splitList(List<Premium> list, int batchSize) {
        List<List<Premium>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }
}
