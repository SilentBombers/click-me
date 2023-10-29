package clickme.transferservice.repository;

import clickme.transferservice.domain.Member;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void batchUpdate(final Chunk<? extends Member> members) {
        String sql = "INSERT INTO member (nickname, click_count) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE click_count = VALUES(click_count)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                Member member = members.getItems()
                        .get(i);
                String nickname = member.nickname().replaceAll("\"", "");
                ps.setString(1, nickname);
                ps.setLong(2, member.clickCount());
            }

            @Override
            public int getBatchSize() {
                return members.size();
            }
        });
    }
}
