package clickme.clickme.util;

import clickme.clickme.domain.Count;
import clickme.clickme.domain.CountLengthCategory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class RectElementManipulator extends ElementManipulator {

    public void configureRectSizeBasedOnCountLength(Element rectElement, final Count count) {
        CountLengthCategory category = CountLengthCategory.findCategory(count.getLength());

        setAttribute(rectElement, "width", category.getWidth());
        setAttribute(rectElement, "height", category.getHeight());
    }
}
