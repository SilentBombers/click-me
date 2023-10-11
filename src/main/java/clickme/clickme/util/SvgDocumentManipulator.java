package clickme.clickme.util;

import clickme.clickme.domain.CountLengthCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
@RequiredArgsConstructor
public class SvgDocumentManipulator {

    private static final String TEXT_X_POSITION = "70";
    private static final String TEXT_Y_POSITION = "45";

    public void drawText(Document doc, String count) {
        Element textElement = doc.getElementById("my-text");
        textElement.setTextContent(count);
        // font-size="24" font-family="Sans,Arial" font-weight="bold"
        textElement.setAttributeNS(null, "font-size", "24");
        textElement.setAttributeNS(null, "font-family", "Arial, Helvetica, sans-serif");
        textElement.setAttributeNS(null, "font-weight", "bold");
        //x="63" y="45"
        textElement.setAttributeNS(null, "x", TEXT_X_POSITION);
        textElement.setAttributeNS(null, "y", TEXT_Y_POSITION);
    }

    public void calculateSizeBasedOnCountLength(Document doc, String count) {
        Element rectElement = doc.getElementById("my-rect");
        CountLengthCategory category = CountLengthCategory.findCategory(Integer.parseInt(count));

        rectElement.setAttributeNS(null, "width", category.getWidth());
        rectElement.setAttributeNS(null, "height", category.getHeight());

        Element emojiElement = doc.getElementById("emoji");
        emojiElement.setAttributeNS(null, "width", category.getWidth());
        emojiElement.setAttributeNS(null, "height", category.getHeight());
    }
}
