package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.count.CountLengthCategory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class TextElementManipulator extends ElementManipulator {

    public void configureDefaultTextElement(Element textElement, Count count) {
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

    public void configureElements(Element textElement, Element circleElement, Count count, Coordinates coors) {
        textElement.setAttribute("x", String.valueOf(coors.textX()));
        textElement.setAttribute("y", String.valueOf(coors.textY()));
        textElement.setAttribute("font-size", "17");
        textElement.setAttribute("fill", "black");
        textElement.setAttribute("font-weight", "bold");
        textElement.setTextContent(count.getValue());

        circleElement.setAttribute("cx", String.valueOf(coors.circleX()));
        circleElement.setAttribute("cy", String.valueOf(coors.circleY()));
    }
}
