package clickme.clickme.util;

import clickme.clickme.domain.Count;
import clickme.clickme.domain.CountLengthCategory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SvgDocumentManipulator {

    private static final String TEXT_ELEMENT_ID = "my-text";
    private static final String RECT_ELEMENT_ID = "my-rect";
    private static final String EMOJI_ELEMENT_ID = "emoji";
    private static final String ANIMATOR_ELEMENT_ID = "animator";
    private static final String TEXT_X_POSITION = "70";
    private static final String TEXT_Y_POSITION = "45";

    public Document drawText(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element textElement = copyDoc.getElementById(TEXT_ELEMENT_ID);
        textElement.setTextContent(count.getString());

        setAttribute(textElement, "font-size", "24");
        setAttribute(textElement, "font-family", "Arial, Helvetica, sans-serif");
        setAttribute(textElement, "font-weight", "bold");

        setAttribute(textElement, "x", TEXT_X_POSITION);
        setAttribute(textElement, "y", TEXT_Y_POSITION);

        return copyDoc;
    }

    public Document calculateSizeBasedOnCountLength(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element rectElement = copyDoc.getElementById(RECT_ELEMENT_ID);
        CountLengthCategory category = CountLengthCategory.findCategory(count.getLength());

        setAttribute(rectElement, "width", category.getWidth());
        setAttribute(rectElement, "height", category.getHeight());

        Element emojiElement = copyDoc.getElementById(EMOJI_ELEMENT_ID);
        setAttribute(emojiElement, "width", category.getWidth());
        setAttribute(emojiElement, "height", category.getHeight());

        Element animatorElement = copyDoc.getElementById(ANIMATOR_ELEMENT_ID);
        if (count.isEven()) {
            setAttribute(animatorElement, "from", "0 36 36");
            setAttribute(animatorElement, "to", "360 36 36");
        }
        if (!count.isEven()) {
            setAttribute(animatorElement, "to", "0 36 36");
            setAttribute(animatorElement, "from", "360 36 36");
        }

        return copyDoc;
    }

    private void setAttribute(Element element, final String attributeName, final String value) {
        element.setAttributeNS(null, attributeName, value);
    }
}
