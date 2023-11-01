package clickme.transferservice.repository;

import clickme.transferservice.domain.ProfileUpdateMember;
import clickme.transferservice.domain.UpsertMember;
import org.springframework.batch.item.Chunk;

public interface MemberRepository {

    void batchUpdateToUpsertMember(Chunk<? extends UpsertMember> members);

    void batchUpdateToProfileUpdateMember(Chunk<? extends ProfileUpdateMember> members);
}
