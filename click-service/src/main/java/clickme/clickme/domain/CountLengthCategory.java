package clickme.clickme.domain;

import java.util.Arrays;

public enum CountLengthCategory {

    ONE(1,"465", "180"),
    TWO(2, "445", "180"),
    THREE(3, "425", "180"),
    FOUR(4, "405", "180"),
    FIVE(5, "385", "180"),
    GREATER_THAN_FIVE(6,"365", "180");

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
                .filter(category -> category.hasCountLengthCategory(length))
                .findAny()
                .orElse(GREATER_THAN_FIVE);
    }

    private boolean hasCountLengthCategory(final int length) {
        return this.length == length;
    }

    public int getLength() {
        return length;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}
