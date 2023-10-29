package clickme.clickme.domain;

public class Count {

    private static final String MAX_COUNT = "99999+";
    private static final long MAX_COUNT_VALUE = 99999L;

    private final Long number;

    public Count(final Long number) {
        this.number = number;
    }

    public boolean isEven() {
        return this.number % 2 == 0 ? true : false;
    }

    public boolean isZero() {
        return this.number == 0;
    }

    public Count increase() {
        return new Count(this.number + 1);
    }

    public String getString() {
        if (this.number >= MAX_COUNT_VALUE) {
            return MAX_COUNT;
        }
        return String.valueOf(this.number);
    }

    public int getLength() {
        return String.valueOf(this.number)
                .length();
    }
}
