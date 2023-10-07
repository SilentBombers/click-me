package clickme.clickme.domain;

public enum CountLengthCategory {

    LESS_OR_EQUAL_TO_FOUR("120", "70"),
    GREATER_THAN_FOUR("140", "70");

    private final String width;
    private final String height;

    CountLengthCategory(String width, String height) {
        this.width = width;
        this.height = height;
    }

    public static CountLengthCategory findCategory(int length) {
        if (length <= 4) {
            return LESS_OR_EQUAL_TO_FOUR;
        } else {
            return GREATER_THAN_FOUR;
        }
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }


}
