package clickme.transferservice.repository;

import clickme.transferservice.job.member.dto.UpsertMember;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpsertMemberBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<? extends UpsertMember> members;

    public UpsertMemberBatchPreparedStatementSetter(Chunk<? extends UpsertMember> members) {
        this.members = members.getItems();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        UpsertMember upsertMember = members.get(i);
        ps.setString(1, upsertMember.nickname());
        ps.setLong(2, upsertMember.clickCount());
    }

    @Override
    public int getBatchSize() {
        return members.size();
    }
}
