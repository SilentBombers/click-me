package clickme.clickme.svg.domain.count;

import java.util.Arrays;

public enum CountLengthCategory {

    ONE(1,"390", "165"),
    TWO(2, "378", "165"),
    THREE(3, "366", "165"),
    FOUR(4, "354", "165"),
    FIVE(5, "342", "165"),
    GREATER_THAN_FIVE(6,"330", "165");

    private final int length;
    private final String x;
    private final String y;


    CountLengthCategory(final int length, final String x, final String y) {
        this.length = length;
        this.x = x;
        this.y = y;
    }

    public static CountLengthCategory findCategory(final int length) {
        return Arrays.stream(CountLengthCategory.values())
                .filter(category -> category.hasCountLength(length))
                .findAny()
                .orElse(GREATER_THAN_FIVE);
    }

    private boolean hasCountLength(final int length) {
        return this.length == length;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}
