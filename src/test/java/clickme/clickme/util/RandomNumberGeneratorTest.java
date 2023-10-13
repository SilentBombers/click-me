package clickme.clickme.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RandomNumberGeneratorTest {

    @Test
    @DisplayName("랜덤으로 번호를 생성한다.")
    void createRandomNumber() {
        RandomNumberGenerator randomNumberGenerator = new EmojiRandomIndexGenerator();

        final int num = randomNumberGenerator.generator(3);

        assertThat(num).isLessThan(3);
    }
}
