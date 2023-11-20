package clickme.clickme.svg.domain.document;

import clickme.clickme.svg.domain.count.Count;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class AnimateElementManipulator extends ElementManipulator {

    public void configureAnimateDirection(Element animateElement, final Count count) {
        String from, to;
        if (count.isEven()) {
            from = "0 143.5 145.5";
            to = "360 143.5 145.5";
        } else {
            from = "360 143.5 145.5";
            to = "0 143.5 145.5";
        }
        setAttribute(animateElement, "from", from);
        setAttribute(animateElement, "to", to);
    }
}
