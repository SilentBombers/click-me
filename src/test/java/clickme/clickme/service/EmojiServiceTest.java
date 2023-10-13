package clickme.clickme.service;

import clickme.clickme.repository.HeartMemoryRepository;
import clickme.clickme.repository.HeartRepository;
import clickme.clickme.repository.MemberRepository;
import clickme.clickme.util.SvgDocumentFactory;
import clickme.clickme.util.SvgDocumentManipulator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class EmojiServiceTest {

    private static final String SEUNGPANG = "seungpang";

    private EmojiService emojiService;
    private HeartRepository heartRepository;

    @Autowired
    private SvgDocumentFactory svgDocumentFactory;

    @Autowired
    private SvgDocumentManipulator svgDocumentManipulator;

    @BeforeEach
    void setUp() {
        heartRepository = new HeartMemoryRepository();
        emojiService = new EmojiService(heartRepository, svgDocumentFactory, svgDocumentManipulator);
        heartRepository.add(SEUNGPANG);
    }

    @Test
    @DisplayName("클릭 했을 경우 카운트가 오르고 svg 이미지가 정상적으로 호출된다.")
    void heart() throws IOException, TransformerException {
        final String heart = emojiService.heart(SEUNGPANG);

        assertAll(
                () -> assertThat(heartRepository.findById(SEUNGPANG)).isEqualTo(1L),
                () -> assertThat(heart.contains("emoji")).isTrue()
        );
    }

    @Test
    @DisplayName("클릭 카운트가 99999를 초과할 경우 99999+로 출력한다")
    void shouldReturnMaxCountWhenClickCountExceedsLimit() throws IOException, TransformerException {
        for (int i = 0; i < 99999; i++) {
            heartRepository.increaseCount(SEUNGPANG);
        }

        final String heart = emojiService.heart(SEUNGPANG);

        assertThat(heart.contains("99999+")).isTrue();
    }

    @Test
    @DisplayName("id로 랭크를 조회한다.")
    void findRankByClicks() {
        final Long rank = emojiService.findRankByClicks(SEUNGPANG);

        assertThat(rank).isEqualTo(1L);

        String id = "angie";
        heartRepository.add(id);
        heartRepository.increaseCount(id);
        heartRepository.increaseCount(id);

        assertThat(emojiService.findRankByClicks(SEUNGPANG)).isEqualTo(2L);
    }

    @Test
    @DisplayName("없는 id로 랭크를 조회하면 예외가 발생 한다.")
    void shouldThrowExceptionWhenFindingRankByNonexistentId() {
        assertThatThrownBy(() -> emojiService.findRankByClicks("angie"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("실시간 랭킹 목록을 조회한다.")
    void findRealTimeRanking() {
        heartRepository.add("angie");
        heartRepository.add("chunsik");

        Set<String> ranking = Set.of(SEUNGPANG, "angie", "chunsik");
        assertThat(emojiService.findRealTimeRanking(1, 3))
                .containsAll(ranking);
    }
}