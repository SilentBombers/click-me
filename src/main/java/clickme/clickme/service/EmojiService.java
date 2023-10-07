package clickme.clickme.service;

import clickme.clickme.domain.CountLengthCategory;
import clickme.clickme.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private static final String EMOJI_PATH = "classpath:static/images/emoji_";
    private static final String EMOJI_FORMAT = ".svg";

    private final HeartRepository heartRepository;
    private final ResourceLoader resourceLoader;

    private Long addAndGetCount(String URI) {
        Long count = heartRepository.findById(URI);
        if (count == 0L) {
            heartRepository.add(URI);
        }
        heartRepository.increaseCount(URI);
        return count + 1L;
    }

    public String heart(String id) throws IOException, TransformerException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        String svgPath = createEmojiPath();
        Document doc = createDocument(svgPath, factory);

        String count = getClickCount(id);
        drawText(doc, count);
        calculateSizeBasedOnCountLength(doc, count);

        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    private String createEmojiPath() {
        return EMOJI_PATH + getRandomIndex() + EMOJI_FORMAT;
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

    private void drawText(Document doc, String count) {
        Element textElement = doc.getElementById("my-text");
        textElement.setTextContent(count);
    }

    private void calculateSizeBasedOnCountLength(Document doc, String count) {
        Element rectElement = doc.getElementById("my-rect");
        CountLengthCategory category = CountLengthCategory.findCategory(Integer.parseInt(count));

        rectElement.setAttributeNS(null, "width", category.getWidth());
        rectElement.setAttributeNS(null, "height", category.getHeight());
    }

    private int getRandomIndex() {
        final List<Integer> randomIndexes = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
        Collections.shuffle(randomIndexes);

        return randomIndexes.get(0);
    }

    public Long findRankByClicks(String id) {
        return heartRepository.findRankByClicks(id);
    }

    public Set<String> findRealTimeRanking(int start, int end) {
        return heartRepository.findRealTimeRanking(start, end);
    }
}
