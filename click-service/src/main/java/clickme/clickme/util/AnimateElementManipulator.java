package clickme.clickme.util;

import clickme.clickme.domain.Count;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class AnimateElementManipulator extends ElementManipulator {

    public void configureAnimateDirection(Element animateElement, final Count count) {
        String from, to;
        if (count.isEven()) {
            from = "0 36 36";
            to = "360 36 36";
        } else {
            from = "360 36 36";
            to = "0 36 36";
        }
        setAttribute(animateElement, "from", from);
        setAttribute(animateElement, "to", to);
    }
}
