package clickme.clickme.svg.application;

import clickme.clickme.common.ErrorCode;
import clickme.clickme.ranking.domain.DailyClickMemoryRepository;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.Member;
import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.RankingMemoryRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.svg.application.exception.SvgException;
import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgTransformer;
import clickme.clickme.svg.domain.document.strategy.DefaultSvgGenerationStrategy;
import clickme.clickme.svg.domain.document.strategy.GcsSvgGenerationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SvgImageServiceTest {

    private static final String TEST_USER = "testuser";
    private static final String EXPECTED_SVG = "<svg>test content</svg>";

    private SvgImageService svgImageService;

    private RankingRepository rankingRepository;
    private DailyClickRepository dailyClickRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SvgTransformer svgTransformer;

    @Mock
    private DefaultSvgGenerationStrategy defaultSvgGenerationStrategy;

    @Mock
    private GcsSvgGenerationStrategy gcsSvgGenerationStrategy;

    @Mock
    private Document mockDocument;

    @BeforeEach
    void setUp() {
        rankingRepository = new RankingMemoryRepository();
        dailyClickRepository = new DailyClickMemoryRepository();

        svgImageService = new SvgImageService(
                rankingRepository,
                dailyClickRepository,
                memberRepository,
                svgTransformer,
                defaultSvgGenerationStrategy,
                gcsSvgGenerationStrategy
        );

        rankingRepository.add(TEST_USER);
    }

    @Test
    @DisplayName("클릭 했을 경우 카운트가 오르고 기본 전략이 사용되어 SVG 이미지가 생성된다")
    void generateClickableSvgImageWithDefaultStrategy() throws IOException, TransformerException {
        // given
        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.empty());
        when(defaultSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenReturn(mockDocument);
        when(svgTransformer.transform(mockDocument)).thenReturn(EXPECTED_SVG);

        // when
        final String svg = svgImageService.generateClickableSvgImage(TEST_USER);

        // then
        assertAll(
                () -> assertThat(rankingRepository.findByName(TEST_USER)).isEqualTo(1L),
                () -> assertThat(svg).isEqualTo(EXPECTED_SVG),
                () -> verify(defaultSvgGenerationStrategy, times(1)).generateSvg(any(Count.class), eq(TEST_USER)),
                () -> verify(gcsSvgGenerationStrategy, times(0)).generateSvg(any(Count.class), anyString())
        );
    }

    @Test
    @DisplayName("클릭 했을 경우 카운트가 오르고 GCS 전략이 사용되어 SVG 이미지가 생성된다")
    void generateClickableSvgImageWithGcsStrategy() throws IOException, TransformerException {
        // given
        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.of("custom-image.svg"));
        when(gcsSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenReturn(mockDocument);
        when(svgTransformer.transform(mockDocument)).thenReturn(EXPECTED_SVG);

        // when
        final String svg = svgImageService.generateClickableSvgImage(TEST_USER);

        // then
        assertAll(
                () -> assertThat(rankingRepository.findByName(TEST_USER)).isEqualTo(1L),
                () -> assertThat(svg).isEqualTo(EXPECTED_SVG),
                () -> verify(gcsSvgGenerationStrategy, times(1)).generateSvg(any(Count.class), eq(TEST_USER)),
                () -> verify(defaultSvgGenerationStrategy, times(0)).generateSvg(any(Count.class), anyString())
        );
    }

    @Test
    @DisplayName("카운트가 오르지 않고 SVG 이미지가 정상적으로 생성된다")
    void generateNonClickableSvgImage() throws IOException, TransformerException {
        // given
        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.empty());
        when(defaultSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenReturn(mockDocument);
        when(svgTransformer.transform(mockDocument)).thenReturn(EXPECTED_SVG);

        // when
        final String svg = svgImageService.generateNonClickableSvgImage(TEST_USER, "null");

        // then
        assertAll(
                () -> assertThat(rankingRepository.findByName(TEST_USER)).isEqualTo(0L),
                () -> assertThat(svg).isEqualTo(EXPECTED_SVG)
        );
    }

    @Test
    @DisplayName("SVG URL이 제공되면 멤버 정보를 업데이트한다")
    void updateMemberSvgUrlWhenProvided() throws IOException, TransformerException {
        // given
        final String svgUrl = "new-image.svg";
        final Member mockMember = mock(Member.class);

        when(memberRepository.getMemberByName(TEST_USER)).thenReturn(mockMember);
        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.empty());
        when(defaultSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenReturn(mockDocument);
        when(svgTransformer.transform(mockDocument)).thenReturn(EXPECTED_SVG);

        // when
        final String svg = svgImageService.generateNonClickableSvgImage(TEST_USER, svgUrl);

        // then
        assertAll(
                () -> verify(mockMember).updateSvgUrl(svgUrl),
                () -> verify(memberRepository).save(mockMember),
                () -> assertThat(svg).isEqualTo(EXPECTED_SVG)
        );
    }

    @Test
    @DisplayName("전략에서 예외가 발생하면 SvgException으로 래핑된다")
    void wrapExceptionFromStrategies() throws IOException, TransformerException {
        // given
        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.empty());
        when(defaultSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenThrow(new IOException("테스트 예외"));

        // when & then
        SvgException exception = assertThrows(SvgException.class,
                () -> svgImageService.generateClickableSvgImage(TEST_USER));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("클릭 카운트가 99999를 초과할 경우 99999+로 출력한다")
    void shouldReturnMaxCountWhenClickCountExceedsLimit() throws IOException, TransformerException {
        // given
        for (int i = 0; i < 99999; i++) {
            rankingRepository.increaseCount(TEST_USER);
        }

        when(memberRepository.findSvgImageName(TEST_USER)).thenReturn(Optional.empty());

        // Count 클래스의 increase() 메서드 호출 후 반환되는 Count 객체는 99999+로 변환된 값을 가짐
        when(defaultSvgGenerationStrategy.generateSvg(any(Count.class), eq(TEST_USER))).thenReturn(mockDocument);
        when(svgTransformer.transform(mockDocument)).thenReturn("<svg>99999+</svg>");

        // when
        final String svg = svgImageService.generateClickableSvgImage(TEST_USER);

        // then
        assertThat(svg).contains("99999+");
    }
}
