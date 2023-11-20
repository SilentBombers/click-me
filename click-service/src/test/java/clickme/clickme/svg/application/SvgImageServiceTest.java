package clickme.clickme.svg.application;

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

    @Autowired
    private SvgDocumentFactory svgDocumentFactory;

    @Autowired
    private SvgDocumentManipulator svgDocumentManipulator;

    @BeforeEach
    void setUp() {
        rankingRepository = new RankingMemoryRepository();
        svgImageService = new SvgImageService(rankingRepository, svgDocumentFactory, svgDocumentManipulator);
        rankingRepository.add(SEUNGPANG);
    }

    @Test
    @DisplayName("클릭 했을 경우 카운트가 오르고 svg 이미지가 정상적으로 호출된다.")
    void heart() throws IOException, TransformerException {
        final String heart = svgImageService.generateSvgImage(SEUNGPANG);

        assertAll(
                () -> assertThat(rankingRepository.findByName(SEUNGPANG)).isEqualTo(1L),
                () -> assertThat(heart.contains("emoji")).isTrue()
        );
    }

    @Test
    @DisplayName("클릭 카운트가 99999를 초과할 경우 99999+로 출력한다")
    void shouldReturnMaxCountWhenClickCountExceedsLimit() throws IOException, TransformerException {
        for (int i = 0; i < 99999; i++) {
            rankingRepository.increaseCount(SEUNGPANG);
        }

        final String heart = svgImageService.generateSvgImage(SEUNGPANG);

        assertThat(heart.contains("99999+")).isTrue();
    }
}
