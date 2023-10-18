package clickme.clickme.service;

import clickme.clickme.controller.api.response.RankingResponse;
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

    private static final String MAX_COUNT = "99999+";
    private static final long MAX_COUNT_VALUE = 99999L;

    private final HeartRepository heartRepository;
    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;

    public String heart(final String id) throws IOException, TransformerException {
        final Document doc = svgDocumentFactory.createEmojiDocument();

        final String count = getClickCount(id);
        final Document textDrawnDoc = svgDocumentManipulator.drawText(doc, count);
        final Document sizeChangedDoc = svgDocumentManipulator.calculateSizeBasedOnCountLength(textDrawnDoc, count);

        return transformSvgToString(sizeChangedDoc);
    }

    private String getClickCount(final String URI) {
        final Long count = addAndGetCount(URI);
        if (count > MAX_COUNT_VALUE) {
            return MAX_COUNT;
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

    public List<RankingResponse> findRealTimeRanking(final long start, final long end) {
        return heartRepository.findRealTimeRanking(start, end);
    }
}
