package clickme.clickme.util;

import clickme.clickme.domain.Count;
import clickme.clickme.domain.CountLengthCategory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class EmojiElementManipulator extends ElementManipulator {

    private static final String VIEW_BOX = "0 0 %s %s";

    public void configureEmojiSizeBasedOnCountLength(Element rectElement, final Count count) {
        CountLengthCategory category = CountLengthCategory.findCategory(count.getLength());

        setAttribute(rectElement, "viewBox", createViewBox(category));
    }

    private String createViewBox(CountLengthCategory category) {
        return VIEW_BOX.formatted(category.getWidth(), category.getHeight());
    }
}
