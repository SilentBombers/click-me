package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.util.RandomNumberGenerator;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SvgDocumentFactory {

    private static final String EMOJI_PATH = "classpath:static/images/emoji_";
    private static final String EMOJI_FORMAT = ".svg";
    private static final int NUMBER_OF_DOCUMENTS = 25;

    private final ResourceLoader resourceLoader;
    private final RandomNumberGenerator emojiRandomIndexGenerator;
    private final SAXSVGDocumentFactory factory;

    public SvgDocumentFactory(final ResourceLoader resourceLoader, final RandomNumberGenerator emojiRandomIndexGenerator) {
        this.resourceLoader = resourceLoader;
        this.emojiRandomIndexGenerator = emojiRandomIndexGenerator;
        this.factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
    }

    public Document createEmojiDocument() throws IOException {
        final String svgPath = createEmojiPath();
        return createSvgDocument(svgPath);
    }

    private String createEmojiPath() {
        return EMOJI_PATH + emojiRandomIndexGenerator.generator(NUMBER_OF_DOCUMENTS) + EMOJI_FORMAT;
    }

    private Document createSvgDocument(final String svgPath) throws IOException {
        final Resource resource = resourceLoader.getResource(svgPath);
        return factory.createDocument(resource.getURI().toString());
    }

    public Document createSvgDocumentFromString(final String svgContent) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8));
        return factory.createDocument(null, inputStream);
    }
}
