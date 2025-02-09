package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.util.CoordinatesCalculator;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SvgDocumentManipulator {

    private static final String TEXT_ELEMENT_ID = "my-text";
    private static final String CIRCLE_ELEMENT_ID = "my-circle";
    private static final String ANIMATE_ELEMENT_ID = "animate";

    private final TextElementManipulator textElementManipulator;
    private final AnimateElementManipulator animateElementManipulator;

    public SvgDocumentManipulator(final TextElementManipulator textElementManipulator,
                                  final AnimateElementManipulator animateElementManipulator) {
        this.textElementManipulator = textElementManipulator;
        this.animateElementManipulator = animateElementManipulator;
    }

    public Document drawDefaultText(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element textElement = copyDoc.getElementById(TEXT_ELEMENT_ID);
        textElementManipulator.configureDefaultTextElement(textElement, count);

        return copyDoc;
    }

    public Document drawText(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element textElement = copyDoc.getElementById(TEXT_ELEMENT_ID);
        Element circleElement = copyDoc.getElementById(CIRCLE_ELEMENT_ID);

        final Element svgRoot = doc.getDocumentElement();
        final int width = Integer.parseInt(svgRoot.getAttribute("width"));
        final int height = Integer.parseInt(svgRoot.getAttribute("height"));
        final Coordinates coords = CoordinatesCalculator.calculateCoordinates(width, height);
        textElementManipulator.configureElements(textElement, circleElement, count, coords);

        return copyDoc;
    }

    public Document executeEffect(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);

        Element animatorElement = copyDoc.getElementById(ANIMATE_ELEMENT_ID);
        animateElementManipulator.configureAnimateDirection(animatorElement, count);

        return copyDoc;
    }
}
