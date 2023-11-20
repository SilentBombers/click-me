package clickme.clickme.util;

import clickme.clickme.domain.Count;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SvgDocumentManipulator {

    private static final String TEXT_ELEMENT_ID = "my-text";
    private static final String ANIMATE_ELEMENT_ID = "animate";

    private final TextElementManipulator textElementManipulator;
    private final AnimateElementManipulator animateElementManipulator;

    public SvgDocumentManipulator(final TextElementManipulator textElementManipulator,
                                  final AnimateElementManipulator animateElementManipulator) {
        this.textElementManipulator = textElementManipulator;
        this.animateElementManipulator = animateElementManipulator;
    }

    public Document drawText(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element textElement = copyDoc.getElementById(TEXT_ELEMENT_ID);
        textElementManipulator.configureTextElement(textElement, count);

        return copyDoc;
    }

    public Document executeEffect(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);

        Element animatorElement = copyDoc.getElementById(ANIMATE_ELEMENT_ID);
        animateElementManipulator.configureAnimateDirection(animatorElement, count);

        return copyDoc;
    }
}
