package clickme.clickme.service;

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
        Element element = doc.getDocumentElement();

        String count = getClickCount(id);
        Element textElement = drawText(doc, element, count);
        Element rectElement = drawRect(doc, element);
        calculateSizeBasedOnCountLength(count, rectElement);

        appendElement(element, rectElement, textElement);
        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    private String createEmojiPath() {
        return "classpath:static/images/emoji_" + getRandomIndex() + ".svg";
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

    private static Element drawText(Document doc, Element element, String count) {
        Element textElement = doc.createElementNS(element.getNamespaceURI(), "text");
        textElement.setAttributeNS(null, "x", "63");
        textElement.setAttributeNS(null, "y", "45");
        textElement.setAttributeNS(null, "font-size", "22");
        textElement.setTextContent(count);
        return textElement;
    }

    private Element drawRect(Document doc, Element element) {
        Element rectElement = doc.createElementNS(element.getNamespaceURI(), "rect");
        rectElement.setAttributeNS(null, "fill", "#e5f9ff");
        rectElement.setAttributeNS(null, "x", "0");
        rectElement.setAttributeNS(null, "y", "0");
        rectElement.setAttributeNS(null, "rx", "20");
        rectElement.setAttributeNS(null, "ry", "20");
        return rectElement;
    }

    private void calculateSizeBasedOnCountLength(String count, Element element) {
        if (count.length() <= 4) {
            element.setAttributeNS(null,"width", "120");
            element.setAttributeNS(null,"height", "70");
        }
        if (count.length() > 4) {
            element.setAttributeNS(null,"with", "140");
            element.setAttributeNS(null,"height", "70");
        }
    }

    private static void appendElement(Element element, Element rectElement, Element textElement) {
        element.insertBefore(rectElement, element.getFirstChild());
        element.appendChild(textElement);
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
