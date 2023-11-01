package clickme.transferservice.repository;

import clickme.transferservice.domain.ProfileUpdateMember;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProfileUpdateMemberBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<? extends ProfileUpdateMember> members;

    public ProfileUpdateMemberBatchPreparedStatementSetter(Chunk<? extends ProfileUpdateMember> members) {
        this.members = members.getItems();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ProfileUpdateMember member = members.get(i);
        ps.setString(1, member.profileImageUrl());
        ps.setString(2, member.nickname());
    }

    @Override
    public int getBatchSize() {
        return members.size();
    }
}
