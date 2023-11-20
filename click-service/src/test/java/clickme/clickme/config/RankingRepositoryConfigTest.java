package clickme.clickme.config;

import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RankingRepositoryConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HeartRepositoryConfig.class));

    @Test
    @DisplayName("HeartRepository빈이 없으면 HeartMemoryRepository로 빈을 생성한다.")
    void whenNoExistingBean_thenHeartMemoryRepositoryShouldBeCreated() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RankingRepository.class);
            assertThat(context.getBean(RankingRepository.class)).isInstanceOf(RankingMemoryRepository.class);
        });
    }
}
