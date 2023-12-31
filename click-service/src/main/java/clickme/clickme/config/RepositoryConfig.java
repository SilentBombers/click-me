package clickme.clickme.config;

import clickme.clickme.ranking.domain.DailyClickMemoryRepository;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    @ConditionalOnMissingBean
    public RankingRepository rankingRepository() {
        return new RankingMemoryRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public DailyClickRepository dailyClickRepository() {
        return new DailyClickMemoryRepository();
    }
}
