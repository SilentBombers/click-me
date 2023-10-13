package clickme.clickme.util;

import clickme.clickme.domain.CountLengthCategory;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SvgDocumentManipulatorTest {

    private SvgDocumentManipulator svgDocumentManipulator;
    private ResourceLoader resourceLoader;

    @BeforeEach
    void setUp() {
        svgDocumentManipulator = new SvgDocumentManipulator();
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    @DisplayName("text를 정상적으로 작성한다.")
    void drawText() throws IOException {
        Document doc = createDocument();

        final String count = "10";
        final Document resultDoc = svgDocumentManipulator.drawText(doc, count);

        assertThat(resultDoc.getElementById("my-text")
                .getTextContent()).isEqualTo(count);
    }

    @ParameterizedTest(name = "카운트 수가 {0}인 경우")
    @ValueSource(strings = {"1","10","100","1000","10000"})
    @DisplayName("카운트 수의 길이에 따라 너비와 높이가 정상적으로 설정된다")
    void calculateSizeBasedOnCountLength(final String count) throws IOException {
        final Document doc = createDocument();

        final Document resultDoc = svgDocumentManipulator.calculateSizeBasedOnCountLength(doc, count);

        final Element rectElement = resultDoc.getElementById("my-rect");
        final Element emojiElement = resultDoc.getElementById("emoji");
        assertAll(
                () -> assertThat(rectElement.getAttribute("width"))
                        .isEqualTo(CountLengthCategory.findCategory(count.length()).getWidth()),
                () -> assertThat(rectElement.getAttribute("height"))
                        .isEqualTo(CountLengthCategory.findCategory(count.length()).getHeight()),
                () -> assertThat(emojiElement.getAttribute("width"))
                        .isEqualTo(CountLengthCategory.findCategory(count.length()).getWidth()),
                () -> assertThat(emojiElement.getAttribute("height"))
                        .isEqualTo(CountLengthCategory.findCategory(count.length()).getHeight())
        );
    }

    private Document createDocument() throws IOException {
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        final Resource resource = resourceLoader.getResource("classpath:static/images/emoji_1.svg");
        final String uri = resource.getURI()
                .toString();
        return factory.createDocument(uri);
    }
}
