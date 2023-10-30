package clickme.transferservice.job.member;

import clickme.transferservice.domain.UpsertMember;
import clickme.transferservice.repository.MemberRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class MysqlItemWriter implements ItemWriter<UpsertMember> {

    private MemberRepository memberRepository;

    public MysqlItemWriter(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void write(final Chunk<? extends UpsertMember> members) {
        System.out.println("이건???");
        memberRepository.batchUpdateToUpsertMember(members);
    }
}
