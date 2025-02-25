package school.faang.user_service.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class PremiumUserDeletionAsyncConfig {

    @Value("${async.remove-premium.corePoolSize}")
    private int corePoolSize;

    @Value("${async.remove-premium.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.remove-premium.queueCapacity}")
    private int queueCapacity;

    @Bean(name = "premiumUserDeletionTaskExecutor")
    public ThreadPoolTaskExecutor premiumUserDeletionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("PremiumUserDeletionAsync-");
        executor.initialize();
        return executor;
    }
}
