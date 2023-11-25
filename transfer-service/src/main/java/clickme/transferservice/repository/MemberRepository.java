package clickme.transferservice.repository;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.ProfileUpdateMember;
import clickme.transferservice.job.member.dto.UpsertMember;
import org.springframework.batch.item.Chunk;

public interface MemberRepository {

    void batchUpdateToUpsertMember(Chunk<? extends UpsertMember> members);

    void batchUpdateToDailyClickCounts(Chunk<? extends DailyClickCount> members);

    void batchUpdateToProfileUpdateMember(Chunk<? extends ProfileUpdateMember> members);
}
