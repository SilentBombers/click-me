package clickme.clickme.util;

import org.w3c.dom.Element;

public abstract class ElementManipulator {

    protected void setAttribute(Element element,
                                final String attributeName,
                                final String value) {
        element.setAttributeNS(null,
                attributeName,
                value);
    }
}
