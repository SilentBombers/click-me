package clickme.clickme.svg.application;

import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

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

    public String generateSvgImage(final String id) throws IOException, TransformerException {
        final Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = getClickCount(id);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document animatedDoc = svgDocumentManipulator.executeEffect(textDrawnDoc, count);

        return transformSvgToString(animatedDoc);
    }

    private Count getClickCount(final String URI) {
        return addAndGetCount(URI);
    }

    private Count addAndGetCount(final String URI) {
        final Count count = new Count(rankingRepository.findByName(URI));
        if (count.isZero()) {
            rankingRepository.add(URI);
            dailyClickRepository.add(URI);
        }
        increaseCount(URI);
        return count.increase();
    }

    private void increaseCount(final String URI) {
        rankingRepository.increaseCount(URI);
        dailyClickRepository.increaseCount(URI);
    }

    private String transformSvgToString(final Document doc) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }
}
