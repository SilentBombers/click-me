package clickme.clickme.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

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
                    assertThat(heartRepository.findById(id)).isZero();
                    heartRepository.increaseCount(id);
                    assertThat(heartRepository.findById(id)).isEqualTo(1L);
                }),
                DynamicTest.dynamicTest("id 기준으로 정상적으로 조회한다.", () -> {
                    String id = "angie";
                    heartRepository.add(id);
                    assertThat(heartRepository.findById(id)).isNotNull();
                }),
                DynamicTest.dynamicTest("id로 클릭 카운트 랭킹을 가져온다.", () -> {
                    String seungpang = "seungpang";
                    assertThat(heartRepository.findRankByClicks(seungpang)).isEqualTo(1L);

                    String angie = "angie";
                    heartRepository.increaseCount(angie);
                    heartRepository.increaseCount(angie);
                    heartRepository.increaseCount(angie);
                    assertThat(heartRepository.findRankByClicks(seungpang)).isEqualTo(2L);
                }),
                DynamicTest.dynamicTest("실시간 랭킹 유저 목록을 가져온다.", () -> {
                    Set<String> rankings = Set.of("angie", "seungpang");
                    assertThat(rankings.containsAll(heartRepository.findRealTimeRanking(1, 2))).isTrue();
                })
        );
    }
}
