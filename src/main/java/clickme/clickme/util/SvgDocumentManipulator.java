package clickme.clickme.util;

import clickme.clickme.domain.CountLengthCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
@RequiredArgsConstructor
public class SvgDocumentManipulator {

    private static final String TEXT_ELEMENT_ID = "my-text";
    private static final String RECT_ELEMENT_ID = "my-rect";
    private static final String EMOJI_ELEMENT_ID = "emoji";
    private static final String TEXT_X_POSITION = "70";
    private static final String TEXT_Y_POSITION = "45";

    public void drawText(Document doc, String count) {
        Element textElement = doc.getElementById(TEXT_ELEMENT_ID);
        textElement.setTextContent(count);

        setAttribute(textElement, "font-size", "24");
        setAttribute(textElement, "font-family", "Arial, Helvetica, sans-serif");
        setAttribute(textElement, "font-weight", "bold");

        setAttribute(textElement, "x", TEXT_X_POSITION);
        setAttribute(textElement, "y", TEXT_Y_POSITION);
    }

    public void calculateSizeBasedOnCountLength(Document doc, String count) {
        Element rectElement = doc.getElementById(RECT_ELEMENT_ID);
        CountLengthCategory category = CountLengthCategory.findCategory(Integer.parseInt(count));

        setAttribute(rectElement, "width", category.getWidth());
        setAttribute(null, "height", category.getHeight());

        Element emojiElement = doc.getElementById(EMOJI_ELEMENT_ID);
        setAttribute(emojiElement, "width", category.getWidth());
        setAttribute(emojiElement, "height", category.getHeight());
    }

    private void setAttribute(Element element, String attributeName, String value) {
        element.setAttributeNS(null, attributeName, value);
    }
}
