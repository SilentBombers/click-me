package clickme.clickme.service;

import clickme.clickme.controller.api.response.RankingResponse;
import clickme.clickme.domain.Count;
import clickme.clickme.repository.HeartRepository;
import clickme.clickme.util.SvgDocumentFactory;
import clickme.clickme.util.SvgDocumentManipulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final HeartRepository heartRepository;
    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;

    public String heart(final String id) throws IOException, TransformerException {
        final Document doc = svgDocumentFactory.createEmojiDocument();

        final Count count = getClickCount(id);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document sizeChangedDoc = svgDocumentManipulator.calculateSizeBasedOnCountLength(textDrawnDoc, count);

        return transformSvgToString(sizeChangedDoc);
    }

    private Count getClickCount(final String URI) {
        addIfZero(URI);
        return addAndGetCount(URI);
    }

    private void addIfZero(final String URI) {
        final Count count = new Count(heartRepository.findById(URI));
        if (count.isZero()) {
            heartRepository.add(URI);
        }
    }

    private Count addAndGetCount(final String URI) {
        Count count = new Count(heartRepository.findById(URI));
        if (count.isZero()) {
            heartRepository.add(URI);
            count = count.increase();
        }
        changedCount(URI);
        return count;
    }

    private void changedCount(final String URI) {
        heartRepository.increaseCount(URI);
        heartRepository.saveChanged(URI);
    }

    private String transformSvgToString(final Document doc) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    public Long findRankByClicks(final String id) {
        return heartRepository.findRankByClicks(id);
    }

    public List<RankingResponse> findRealTimeRanking(final int start, final int end) {
        return heartRepository.findRealTimeRanking(start, end);
    }
}
