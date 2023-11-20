package clickme.clickme.ranking.application;

import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.ranking.domain.Member;
import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.exception.NotFoundMemberException;
import clickme.clickme.ranking.ui.response.RankingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RankingServiceTest {

    private static final String SEUNGPANG = "seungpang";
    private static final String ANGIE = "angie";
    private static final String CHUNSIK = "chunsik";

    private RankingService rankingService;
    private RankingRepository rankingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(new Member(0L, SEUNGPANG, "a"));
        memberRepository.save(new Member(0L, ANGIE, "b"));
        memberRepository.save(new Member(0L, CHUNSIK, "c"));

        rankingRepository = new RankingMemoryRepository();
        rankingService = new RankingService(rankingRepository, memberRepository);
        rankingRepository.add(SEUNGPANG);
    }

    @Test
    @DisplayName("id로 랭크를 조회한다.")
    void findRankByClicks() {
        final Long rank = rankingService.findRankByName(SEUNGPANG);

        assertThat(rank).isEqualTo(1L);

        final String id = "angie";
        rankingRepository.add(id);
        rankingRepository.increaseCount(id);
        rankingRepository.increaseCount(id);

        assertThat(rankingService.findRankByName(SEUNGPANG)).isEqualTo(2L);
    }

    @Test
    @DisplayName("없는 id로 랭크를 조회하면 예외가 발생 한다.")
    void shouldThrowExceptionWhenFindingRankByNonexistentId() {
        assertThatThrownBy(() -> rankingService.findRankByName("angie"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("실시간 랭킹 목록을 조회한다.")
    void findRealTimeRanking() {
        rankingRepository.add(ANGIE);
        rankingRepository.add(CHUNSIK);
        rankingRepository.increaseCount(SEUNGPANG);
        rankingRepository.increaseCount(SEUNGPANG);
        rankingRepository.increaseCount(ANGIE);
        final List<RankingResponse> rankings = List.of(
                new RankingResponse(rankingRepository.findRankByName(SEUNGPANG), SEUNGPANG,
                        rankingRepository.findByName(SEUNGPANG), memberRepository.getProfileImageUrlByName(SEUNGPANG)),
                new RankingResponse(rankingRepository.findRankByName(ANGIE), ANGIE,
                        rankingRepository.findByName(ANGIE), memberRepository.getProfileImageUrlByName(ANGIE)),
                new RankingResponse(rankingRepository.findRankByName(CHUNSIK), CHUNSIK,
                        rankingRepository.findByName(CHUNSIK), memberRepository.getProfileImageUrlByName(CHUNSIK))
        );

        assertThat(rankingService.findLiveRanking(0, 3))
                .containsAll(rankings);
    }

    @Test
    @DisplayName("없는 member로 조회할 경우 예외 발생")
    void findProfileImageUrlByNameOrThrow() {
        String invalidName = "invalidName";
        rankingRepository.add(invalidName);

        assertThatThrownBy(() -> rankingService.findLiveRanking(1, 2))
                .isInstanceOf(NotFoundMemberException.class);
    }
}
