package clickme.transferservice.repository;

import clickme.transferservice.domain.Member;
import org.springframework.batch.item.Chunk;

public interface MemberRepository {

    void batchUpdate(Chunk<? extends Member> members);
}
