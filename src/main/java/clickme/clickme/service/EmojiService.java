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

    public String heart(String id) throws IOException, TransformerException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        String svgPath = createEmojiPath();
        Document doc = createDocument(svgPath, factory);

        String count = getClickCount(id);
        svgDocumentManipulator.drawText(doc, count);
        svgDocumentManipulator.calculateSizeBasedOnCountLength(doc, count);

        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    private String createEmojiPath() {
        return EMOJI_PATH + EmojiRandomIndexGenerator.getRandomNumber() + EMOJI_FORMAT;
    }

    private Document createDocument(String svgPath, SAXSVGDocumentFactory factory) throws IOException {
        Resource resource = resourceLoader.getResource(svgPath);
        String uri = resource.getURI().toString();
        Document doc = factory.createDocument(uri);
        return doc;
    }

    private String getClickCount(String URI) {
        Long count = addAndGetCount(URI);
        if (count > 99999) {
            return  "99999+";
        }
        return String.valueOf(count);
    }

    private Long addAndGetCount(String URI) {
        Long count = heartRepository.findById(URI);
        if (count == 0L) {
            heartRepository.add(URI);
        }
        heartRepository.increaseCount(URI);
        return count + 1L;
    }

    public Long findRankByClicks(String id) {
        return heartRepository.findRankByClicks(id);
    }

    public Set<String> findRealTimeRanking(int start, int end) {
        return heartRepository.findRealTimeRanking(start, end);
    }
}
