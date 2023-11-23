package clickme.transferservice.repository;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.ProfileUpdateMember;
import clickme.transferservice.job.member.dto.UpsertMember;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void batchUpdateToUpsertMember(final Chunk<? extends UpsertMember> members) {
        String sql = "INSERT INTO member (name, click_count) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE click_count = VALUES(click_count)";
        jdbcTemplate.batchUpdate(sql, new UpsertMemberBatchPreparedStatementSetter(members));
    }

    @Override
    public void batchUpdateToDailyClickCounts(final Chunk<? extends DailyClickCount> members) {
        String sql = "INSERT INTO daily_click_count (name, date, click_count) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new InsertDailyClickCountBatchPreparedStatementStatementSetter(members));
    }

    @Override
    public void batchUpdateToProfileUpdateMember(final Chunk<? extends ProfileUpdateMember> members) {
        String sql = "UPDATE member SET profile_image_url = ? WHERE nickname = ?";
        jdbcTemplate.batchUpdate(sql, new ProfileUpdateMemberBatchPreparedStatementSetter(members));
    }
}
