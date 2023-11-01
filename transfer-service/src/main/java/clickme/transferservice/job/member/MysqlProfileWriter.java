package clickme.transferservice.job.member;

import clickme.transferservice.domain.ProfileUpdateMember;
import clickme.transferservice.repository.MemberRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class MysqlProfileWriter implements ItemWriter<ProfileUpdateMember> {

    private MemberRepository memberRepository;

    public MysqlProfileWriter(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void write(final Chunk<? extends ProfileUpdateMember> members) {
        memberRepository.batchUpdateToProfileUpdateMember(members);
    }
}
