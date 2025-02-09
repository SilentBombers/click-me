package clickme.clickme.svg.domain.document.strategy;

import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import org.w3c.dom.Document;

import java.io.IOException;

public class DefaultSvgGenerationStrategy implements SvgGenerationStrategy{

    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;

    public DefaultSvgGenerationStrategy(SvgDocumentFactory svgDocumentFactory,
                                        SvgDocumentManipulator svgDocumentManipulator) {
        this.svgDocumentFactory = svgDocumentFactory;
        this.svgDocumentManipulator = svgDocumentManipulator;
    }

    @Override
    public Document generateSvg(final Count count, final String name) throws IOException {
        Document doc = svgDocumentFactory.createEmojiDocument();
        doc = svgDocumentManipulator.drawDefaultText(doc, count);
        doc = svgDocumentManipulator.executeEffect(doc, count);
        return doc;
    }
}
