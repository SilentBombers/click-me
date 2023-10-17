package clickme.transferservice.job.member;

import clickme.transferservice.domain.Member;
import clickme.transferservice.repository.MemberRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class MysqlItemWriter implements ItemWriter<Member> {

    private MemberRepository memberRepository;

    public MysqlItemWriter(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void write(final Chunk<? extends Member> members) {
        memberRepository.batchUpdate(members);
    }
}
