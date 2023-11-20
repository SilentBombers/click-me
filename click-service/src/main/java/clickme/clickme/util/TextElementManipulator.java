package clickme.clickme.util;

import clickme.clickme.domain.Count;
import clickme.clickme.domain.CountLengthCategory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class TextElementManipulator extends ElementManipulator {

    public void configureTextElement(Element textElement, Count count) {
        textElement.setTextContent(count.getValue());

        setAttribute(textElement, "font-size", "50");
        setAttribute(textElement, "font-family", "Arial, Helvetica, sans-serif");
        setAttribute(textElement, "font-weight", "bold");
        setAttribute(textElement, "fill", "white");
        setAttribute(textElement, "stroke", "gray");
        setAttribute(textElement, "stroke-width", "2");

        configureTextPosition(textElement, count);
    }

    private void configureTextPosition(final Element textElement, final Count count) {
        CountLengthCategory category = CountLengthCategory.findCategory(count.getLength());

        setAttribute(textElement, "x", category.getX());
        setAttribute(textElement, "y", category.getY());
    }
}
