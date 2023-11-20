package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.count.CountLengthCategory;
import clickme.clickme.svg.util.EmojiRandomIndexGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
                new AnimateElementManipulator()
        );
        svgDocumentFactory = new SvgDocumentFactory(new DefaultResourceLoader(), new EmojiRandomIndexGenerator());
    }

    @ParameterizedTest(name = "카운트 수가 {0}인 경우")
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 10000, 100000})
    @DisplayName("카운트 수의 길이에 따라 텍스트의 설정값들이 정상적으로 설정된다.")
    void drawText(final long num) throws IOException {
        Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = new Count(num);
        final Document resultDoc = svgDocumentManipulator.drawText(doc, count);

        final Element textElement = resultDoc.getElementById("my-text");
        assertAll(
                () -> assertThat(textElement.getTextContent()).isEqualTo(count.getValue()),
                () -> assertThat(textElement.getAttribute("x"))
                        .isEqualTo(CountLengthCategory.findCategory(count.getLength()).getX()),
                () -> assertThat(textElement.getAttribute("y"))
                        .isEqualTo(CountLengthCategory.findCategory(count.getLength()).getY())
        );
    }

    @ParameterizedTest(name = "카운트 수가 {0}인 경우")
    @CsvSource({
            "1,360 143.5 145.5,0 143.5 145.5",
            "2,0 143.5 145.5,360 143.5 145.5",
            "21,360 143.5 145.5,0 143.5 145.5",
            "44,0 143.5 145.5,360 143.5 145.5",
    })
    @DisplayName("카운트 수의 길이에 따라 텍스트의 x,y 값이 정상적으로 설정된다.")
    void executeEffect(final long num, final String from, final String to) throws IOException {
        Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = new Count(num);
        final Document resultDoc = svgDocumentManipulator.executeEffect(doc, count);

        final Element animateElement = resultDoc.getElementById("animate");
        assertAll(
                () -> assertThat(animateElement.getAttribute("from"))
                        .isEqualTo(from),
                () -> assertThat(animateElement.getAttribute("to"))
                        .isEqualTo(to)
        );
    }
}
