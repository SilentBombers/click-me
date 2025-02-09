package clickme.clickme.svg.util;

import clickme.clickme.svg.domain.document.Coordinates;

public class CoordinatesCalculator {

    private CoordinatesCalculator() {
    }

    public static Coordinates calculateCoordinates(int width, int height) {
        int circleX = (int) (width * 0.80);
        int circleY = (int) (height * 0.20);
        int textX = circleX;
        int textY = (int) (height * 0.25);
        return new Coordinates(circleX, circleY, textX, textY);
    }
}
