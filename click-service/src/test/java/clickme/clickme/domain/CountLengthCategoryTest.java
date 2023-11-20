package clickme.clickme.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CountLengthCategoryTest {

    @ParameterizedTest(name = "길이가 {0}인 경우")
    @CsvSource({
            "1,ONE",
            "2,TWO",
            "3,THREE",
            "4,FOUR",
            "5,FIVE",
            "6,GREATER_THAN_FIVE",
            "7,GREATER_THAN_FIVE"
    })
    @DisplayName("길이에 따라 category를 알맞게 찾아온다.")
    void findCategoryTest(int length, CountLengthCategory expected) {
        final CountLengthCategory category = CountLengthCategory.findCategory(length);
        assertThat(category).isEqualTo(expected);
    }
}
