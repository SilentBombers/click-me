package clickme.clickme.config;

import clickme.clickme.ranking.domain.DailyClickMemoryRepository;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RepositoryConfig.class));

    @Test
    @DisplayName("RankingRepository빈이 없으면 RankingMemoryRepository로 빈을 생성한다.")
    void whenNoExistingBean_thenRankingMemoryRepositoryShouldBeCreated() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RankingRepository.class);
            assertThat(context.getBean(RankingRepository.class)).isInstanceOf(RankingMemoryRepository.class);
        });
    }

    @Test
    @DisplayName("DailyClickRepository빈이 없으면 DailyClickMemoryRepository로 빈을 생성한다.")
    void whenNoExistingBean_thenDailyClickMemoryRepositoryShouldBeCreated() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DailyClickRepository.class);
            assertThat(context.getBean(DailyClickRepository.class)).isInstanceOf(DailyClickMemoryRepository.class);
        });
    }
}
