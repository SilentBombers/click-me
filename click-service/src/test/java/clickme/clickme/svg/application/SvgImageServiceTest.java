package clickme.clickme.svg.application;

import clickme.clickme.ranking.domain.DailyClickMemoryRepository;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SvgImageServiceTest {

    private static final String SEUNGPANG = "seungpang";

    private SvgImageService svgImageService;
    private RankingRepository rankingRepository;
    private DailyClickRepository dailyClickRepository;

    @Autowired
    private SvgDocumentFactory svgDocumentFactory;

    @Autowired
    private SvgDocumentManipulator svgDocumentManipulator;

    @BeforeEach
    void setUp() {
        rankingRepository = new RankingMemoryRepository();
        dailyClickRepository = new DailyClickMemoryRepository();
        svgImageService = new SvgImageService(rankingRepository, dailyClickRepository, svgDocumentFactory, svgDocumentManipulator);
        rankingRepository.add(SEUNGPANG);
    }

    @Test
    @DisplayName("클릭 했을 경우 카운트가 오르고 svg 이미지가 정상적으로 호출된다.")
    void generateClickableSvgImage() throws IOException, TransformerException {
        final String svg = svgImageService.generateClickableSvgImage(SEUNGPANG);

        assertAll(
                () -> assertThat(rankingRepository.findByName(SEUNGPANG)).isEqualTo(1L),
                () -> assertThat(svg.contains("emoji")).isTrue()
        );
    }

    @Test
    @DisplayName("카운트가 오르지 않고 svg 이미지가 정상적으로 호출된다.")
    void generateNonClickableSvgImage() throws IOException, TransformerException {
        final String svg = svgImageService.generateNonClickableSvgImage(SEUNGPANG);

        assertAll(
                () -> assertThat(rankingRepository.findByName(SEUNGPANG)).isEqualTo(0L),
                () -> assertThat(svg.contains("emoji")).isTrue()
        );
    }

    @Test
    @DisplayName("클릭 카운트가 99999를 초과할 경우 99999+로 출력한다")
    void shouldReturnMaxCountWhenClickCountExceedsLimit() throws IOException, TransformerException {
        for (int i = 0; i < 99999; i++) {
            rankingRepository.increaseCount(SEUNGPANG);
        }

        final String svg = svgImageService.generateClickableSvgImage(SEUNGPANG);

        assertThat(svg.contains("99999+")).isTrue();
    }
}
