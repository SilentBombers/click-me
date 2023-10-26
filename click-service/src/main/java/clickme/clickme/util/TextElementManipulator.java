package clickme.clickme.util;

import clickme.clickme.domain.Count;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class TextElementManipulator extends ElementManipulator {

    private static final String TEXT_X_POSITION = "70";
    private static final String TEXT_Y_POSITION = "45";

    public void configureTextElement(Element textElement, Count count) {
        textElement.setTextContent(count.getString());

        setAttribute(textElement, "font-size", "24");
        setAttribute(textElement, "font-family", "Arial, Helvetica, sans-serif");
        setAttribute(textElement, "font-weight", "bold");

        setAttribute(textElement, "x", TEXT_X_POSITION);
        setAttribute(textElement, "y", TEXT_Y_POSITION);
    }
}
