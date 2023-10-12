package clickme.clickme.util;

import java.util.Random;

public class EmojiRandomIndexGenerator {

    private static final int RANDOM_BOUND = 15;
    private static final Random random = new Random();

    private EmojiRandomIndexGenerator() {
    }

    public static int getRandomNumber() {
        return random.nextInt(RANDOM_BOUND);
    }
}
