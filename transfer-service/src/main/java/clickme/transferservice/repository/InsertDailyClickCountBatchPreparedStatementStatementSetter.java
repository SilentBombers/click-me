package clickme.transferservice.repository;

import clickme.transferservice.job.member.dto.DailyClickCount;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertDailyClickCountBatchPreparedStatementStatementSetter implements BatchPreparedStatementSetter {

    private final List<? extends DailyClickCount> members;

    public InsertDailyClickCountBatchPreparedStatementStatementSetter(final Chunk<? extends DailyClickCount> members) {
        this.members = members.getItems();
    }

    @Override
    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
        DailyClickCount dailyClickCount = members.get(i);
        ps.setString(1, dailyClickCount.name());
        ps.setDate(2, Date.valueOf(dailyClickCount.date()));
        ps.setLong(3, dailyClickCount.clickCount());
    }

    @Override
    public int getBatchSize() {
        return members.size();
    }
}
