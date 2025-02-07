package clickme.clickme.svg.domain.document.strategy;

import clickme.clickme.svg.domain.count.Count;
import org.w3c.dom.Document;

import java.io.IOException;

public interface SvgGenerationStrategy {

    Document generateSvg(Count count, String name, String svgUrl) throws IOException;
}
