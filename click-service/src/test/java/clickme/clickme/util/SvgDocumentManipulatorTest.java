package clickme.clickme.util;

import clickme.clickme.domain.Count;
import clickme.clickme.domain.CountLengthCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SvgDocumentManipulatorTest {

    private SvgDocumentManipulator svgDocumentManipulator;
    private SvgDocumentFactory svgDocumentFactory;

    @BeforeEach
    void setUp() {
        svgDocumentManipulator = new SvgDocumentManipulator(
                new TextElementManipulator(),
                new RectElementManipulator(),
                new EmojiElementManipulator(),
                new AnimateElementManipulator()
        );
        svgDocumentFactory = new SvgDocumentFactory(new DefaultResourceLoader(), new EmojiRandomIndexGenerator());
    }

    @Test
    @DisplayName("text를 정상적으로 작성한다.")
    void drawText() throws IOException {
        Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = new Count(10L);
        final Document resultDoc = svgDocumentManipulator.drawText(doc, count);

        assertThat(resultDoc.getElementById("my-text")
                .getTextContent()).isEqualTo(count.getString());
    }

    @ParameterizedTest(name = "카운트 수가 {0}인 경우")
    @ValueSource(ints = {1,10,100,1000,10000})
    @DisplayName("카운트 수의 길이에 따라 너비와 높이가 정상적으로 설정된다")
    void calculateSizeBasedOnCountLength(final long num) throws IOException {
        Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = new Count(num);
        final Document resultDoc = svgDocumentManipulator.calculateSizeBasedOnCountLength(doc, count);

        final Element rectElement = resultDoc.getElementById("my-rect");
        final Element emojiElement = resultDoc.getElementById("emoji");
        final CountLengthCategory category = CountLengthCategory.findCategory(count.getLength());
        assertAll(
                () -> assertThat(rectElement.getAttribute("width"))
                        .isEqualTo(category.getWidth()),
                () -> assertThat(rectElement.getAttribute("height"))
                        .isEqualTo(category.getHeight()),
                () -> assertThat(emojiElement.getAttribute("viewBox"))
                        .isEqualTo(createViewBox(category))
        );
    }

    private String createViewBox(CountLengthCategory category) {
        return "0 0 %s %s".formatted(category.getWidth(), category.getHeight());
    }
}
