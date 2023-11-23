package clickme.clickme.svg.application;

import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class SvgImageService {

    private final RankingRepository rankingRepository;
    private final DailyClickRepository dailyClickRepository;
    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;

    public String generateClickableSvgImage(final String name) throws IOException, TransformerException {
        final Document doc = svgDocumentFactory.createEmojiDocument();
        final Count count = addAndGetCount(name);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document animatedDoc = svgDocumentManipulator.executeEffect(textDrawnDoc, count);
        return transformSvgToString(animatedDoc);
    }

    private Count addAndGetCount(final String name) {
        final Count count = new Count(rankingRepository.findByName(name));
        System.out.println(count.getValue());
        if (count.isZero()) {
            rankingRepository.add(name);
            dailyClickRepository.add(name);
        }
        increaseCount(name);
        return count.increase();
    }

    private void increaseCount(final String name) {
        rankingRepository.increaseCount(name);
        dailyClickRepository.increaseCount(name);
    }

    private String transformSvgToString(final Document doc) throws TransformerException {
        StringWriter writer = new StringWriter();
        final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    public String generateNonClickableSvgImage(final String id) throws IOException, TransformerException {
        final Document doc = svgDocumentFactory.createEmojiDocument();
        final Count count = getClickCount(id);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document animatedDoc = svgDocumentManipulator.executeEffect(textDrawnDoc, count);
        return transformSvgToString(animatedDoc);
    }

    private Count getClickCount(final String URI) {
        return new Count(rankingRepository.findByName(URI));
    }
}
