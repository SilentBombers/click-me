package clickme.clickme.ranking.domain;

import clickme.clickme.ranking.domain.HeartMemoryRepository;
import clickme.clickme.ranking.domain.HeartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class HeartRepositoryTest {

    private final HeartRepository heartRepository = new HeartMemoryRepository();

    @TestFactory
    @DisplayName("HeartRepository 테스트")
    Collection<DynamicTest> dynamicTests() {
        return Arrays.asList(
                DynamicTest.dynamicTest("카운트를 정상적으로 증가시킨다.", () -> {
                    String id = "seungpang";
                    heartRepository.add(id);
                    assertThat(heartRepository.findByName(id)).isZero();
                    heartRepository.increaseCount(id);
                    assertThat(heartRepository.findByName(id)).isEqualTo(1L);
                }),
                DynamicTest.dynamicTest("id 기준으로 정상적으로 조회한다.", () -> {
                    String id = "angie";
                    heartRepository.add(id);
                    assertThat(heartRepository.findByName(id)).isNotNull();
                }),
                DynamicTest.dynamicTest("id로 클릭 카운트 랭킹을 가져온다.", () -> {
                    String seungpang = "seungpang";
                    assertThat(heartRepository.findRankByName(seungpang)).isEqualTo(1L);

                    String angie = "angie";
                    heartRepository.increaseCount(angie);
                    heartRepository.increaseCount(angie);
                    heartRepository.increaseCount(angie);
                    assertThat(heartRepository.findRankByName(seungpang)).isEqualTo(2L);
                }),
                DynamicTest.dynamicTest("실시간 랭킹 유저 목록을 가져온다.", () -> {
                    assertThat((heartRepository.findLiveRanking(0, 2)))
                            .extracting("nickname")
                            .containsExactly("angie", "seungpang");
                })
        );
    }
}
