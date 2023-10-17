package clickme.clickme.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("member의 클릭카운트를 정상적으로 업데이트 한다.")
    void updateClickCount() {
        Member member = new Member("seungpang", 3L);

        member.updateClickCount(5L);

        assertThat(member.getClickCount()).isEqualTo(5L);
    }
}
