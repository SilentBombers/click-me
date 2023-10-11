package clickme.clickme.config;

import clickme.clickme.repository.HeartMemoryRepository;
import clickme.clickme.repository.HeartRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeartRepositoryConfig {

    @Bean
    @ConditionalOnMissingBean
    public HeartRepository heartRepository() {
        return new HeartMemoryRepository();
    }
}