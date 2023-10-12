package clickme.clickme.service;

import clickme.clickme.repository.HeartRepository;
import clickme.clickme.util.EmojiRandomIndexGenerator;
import clickme.clickme.util.SvgDocumentManipulator;
import lombok.RequiredArgsConstructor;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private static final String EMOJI_PATH = "classpath:static/images/emoji_";
    private static final String EMOJI_FORMAT = ".svg";

    private final HeartRepository heartRepository;
    private final ResourceLoader resourceLoader;
    private final SvgDocumentManipulator svgDocumentManipulator;

    public String heart(final String id) throws IOException, TransformerException {
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        final String svgPath = createEmojiPath();
        final Document doc = createDocument(svgPath, factory);

        final String count = getClickCount(id);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document sizeChangedDoc = svgDocumentManipulator.calculateSizeBasedOnCountLength(textDrawnDoc, count);

        final StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(sizeChangedDoc), new StreamResult(writer));

        return writer.toString();
    }

    private String createEmojiPath() {
        return EMOJI_PATH + EmojiRandomIndexGenerator.getRandomNumber() + EMOJI_FORMAT;
    }

    private Document createDocument(final String svgPath, final SAXSVGDocumentFactory factory) throws IOException {
        final Resource resource = resourceLoader.getResource(svgPath);
        final String uri = resource.getURI()
                .toString();
        return factory.createDocument(uri);
    }

    private String getClickCount(final String URI) {
        final Long count = addAndGetCount(URI);
        if (count > 99999) {
            return "99999+";
        }
        return String.valueOf(count);
    }

    private Long addAndGetCount(final String URI) {
        final Long count = heartRepository.findById(URI);
        if (count == 0L) {
            heartRepository.add(URI);
        }
        heartRepository.increaseCount(URI);
        return count + 1L;
    }

    public Long findRankByClicks(final String id) {
        return heartRepository.findRankByClicks(id);
    }

    public Set<String> findRealTimeRanking(final int start, final int end) {
        return heartRepository.findRealTimeRanking(start, end);
    }
}
