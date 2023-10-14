package clickme.clickme.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EmojiRandomIndexGenerator implements RandomNumberGenerator {

    private static final Random random = new Random();

    @Override
    public int generator(final int size) {
        return random.nextInt(size);
    }
}
