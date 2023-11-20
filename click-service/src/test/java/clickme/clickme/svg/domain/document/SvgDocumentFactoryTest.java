package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.util.EmojiRandomIndexGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SvgDocumentFactoryTest {

    @Test
    void createEmojiDocument() throws IOException {
        final SvgDocumentFactory factory = new SvgDocumentFactory(new DefaultResourceLoader(), new EmojiRandomIndexGenerator());

        final Document doc = factory.createEmojiDocument();

        assertThat(doc.getDocumentElement().getTagName()).isEqualTo("svg");
    }
}
