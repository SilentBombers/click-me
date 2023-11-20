package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.UpsertMember;
import clickme.transferservice.repository.MemberRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MysqlItemWriter implements ItemWriter<Pair<UpsertMember, DailyClickCount>> {

    private MemberRepository memberRepository;

    public MysqlItemWriter(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void write(final Chunk<? extends Pair<UpsertMember, DailyClickCount>> pairs) {
        List<UpsertMember> upsertMembers = new ArrayList<>();
        List<DailyClickCount> dailyClickCounts = new ArrayList<>();

        pairs.forEach(pair -> {
            upsertMembers.add(pair.getFirst());
            dailyClickCounts.add(pair.getSecond());
        });

        memberRepository.batchUpdateToUpsertMember(new Chunk<>(upsertMembers));
        memberRepository.batchUpdateToDailyClickCounts(new Chunk<>(dailyClickCounts));
    }
}
