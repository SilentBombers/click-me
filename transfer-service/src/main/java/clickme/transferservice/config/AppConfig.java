package clickme.transferservice.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {

    private static final int POOL_SIZE = 10;

    private ThreadPoolTaskExecutor executor;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(POOL_SIZE);
        executor.setThreadNamePrefix("executor-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }
}
