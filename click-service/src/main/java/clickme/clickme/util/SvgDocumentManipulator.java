package clickme.clickme.util;

import clickme.clickme.domain.Count;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SvgDocumentManipulator {

    private static final String TEXT_ELEMENT_ID = "my-text";
    private static final String RECT_ELEMENT_ID = "my-rect";
    private static final String EMOJI_ELEMENT_ID = "emoji";
    private static final String ANIMATOR_ELEMENT_ID = "animator";

    private final TextElementManipulator textElementManipulator;
    private final RectElementManipulator rectElementManipulator;
    private final EmojiElementManipulator emojiElementManipulator;
    private final AnimateElementManipulator animateElementManipulator;

    public SvgDocumentManipulator(final TextElementManipulator textElementManipulator,
                                  final RectElementManipulator rectElementManipulator,
                                  final EmojiElementManipulator emojiElementManipulator,
                                  final AnimateElementManipulator animateElementManipulator) {
        this.textElementManipulator = textElementManipulator;
        this.rectElementManipulator = rectElementManipulator;
        this.emojiElementManipulator = emojiElementManipulator;
        this.animateElementManipulator = animateElementManipulator;
    }

    public Document drawText(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element textElement = copyDoc.getElementById(TEXT_ELEMENT_ID);
        textElementManipulator.configureTextElement(textElement, count);

        return copyDoc;
    }

    public Document calculateSizeBasedOnCountLength(final Document doc, final Count count) {
        Document copyDoc = (Document) doc.cloneNode(true);
        Element rectElement = copyDoc.getElementById(RECT_ELEMENT_ID);
        rectElementManipulator.configureRectSizeBasedOnCountLength(rectElement, count);

        Element emojiElement = copyDoc.getElementById(EMOJI_ELEMENT_ID);
        emojiElementManipulator.configureEmojiSizeBasedOnCountLength(emojiElement, count);

        Element animatorElement = copyDoc.getElementById(ANIMATOR_ELEMENT_ID);
        animateElementManipulator.configureAnimateDirection(animatorElement, count);

        return copyDoc;
    }
}
