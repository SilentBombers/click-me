package clickme.clickme.config;

import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeartRepositoryConfig {

    @Bean
    @ConditionalOnMissingBean
    public RankingRepository heartRepository() {
        return new RankingMemoryRepository();
    }
}
