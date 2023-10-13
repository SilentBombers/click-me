package clickme.clickme.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CountLengthCategoryTest {

    @ParameterizedTest(name = "길이가 {0}인 경우")
    @ValueSource(ints = {1,2,3,4})
    @DisplayName("4이하일 경우 Category는 LESS_OR_EQUAL_TO_FOUR이다.")
    void findCategoryByLessOrEqualToFour(int length) {
        int num = length;
        assertThat(CountLengthCategory.findCategory(num))
                .isEqualTo(CountLengthCategory.LESS_OR_EQUAL_TO_FOUR);
    }

    @ParameterizedTest(name = "길이가 {0}인 경우")
    @ValueSource(ints = {5,6,10,20,30})
    @DisplayName("4를 초과할 경우 Category는 GREATER_THAN_FOUR이다.")
    void findCategoryByGreaterThanFour(int length) {
        int num = length;
        assertThat(CountLengthCategory.findCategory(num))
                .isEqualTo(CountLengthCategory.GREATER_THAN_FOUR);
    }
}
