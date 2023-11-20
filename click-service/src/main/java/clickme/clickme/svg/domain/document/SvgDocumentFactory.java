package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.util.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SvgDocumentFactory {

    private static final String EMOJI_PATH = "classpath:static/images/emoji_";
    private static final String EMOJI_FORMAT = ".svg";
    private static final int NUMBER_OF_DOCUMENTS = 25;

    private final ResourceLoader resourceLoader;
    private final RandomNumberGenerator emojiRandomIndexGenerator;

    public Document createEmojiDocument() throws IOException {
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        final String svgPath = createEmojiPath();
        return createSvgDocument(svgPath, factory);
    }

    private String createEmojiPath() {
        return EMOJI_PATH + emojiRandomIndexGenerator.generator(NUMBER_OF_DOCUMENTS) + EMOJI_FORMAT;
    }

    private Document createSvgDocument(final String svgPath,
                                       final SAXSVGDocumentFactory factory) throws IOException {
        final Resource resource = resourceLoader.getResource(svgPath);
        return factory.createDocument(resource.getURI().toString());
    }
}
