package clickme.clickme.service;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.domain.Member;
import clickme.clickme.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Conditional(RedisConnectionCondition.class)
public class DataTransferService {

    private static final int BATCH_SIZE = 1000;
    private static final String KEY = "clicks";

    private final ZSetOperations<String, String> zSetOperations;
    private final MemberRepository memberRepository;

    public DataTransferService(final RedisTemplate<String, String> redisTemplate, final MemberRepository memberRepository) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.memberRepository = memberRepository;
    }

    @Scheduled(fixedRate = 7200000)
    @Transactional
    public void transferData() {
        long startIndex = 0;

        while (true) {
            Set<String> nicknameBatch = zSetOperations.range(KEY, startIndex, startIndex + BATCH_SIZE - 1);
            if (nicknameBatch.isEmpty()) {
                break;
            }

            List<Member> memberBatch = new ArrayList<>();
            for (String nickname : nicknameBatch) {
                addFoundMember(nickname, memberBatch);
            }

            memberRepository.saveAll(memberBatch);
            startIndex += BATCH_SIZE;
        }
    }

    private void addFoundMember(final String nickname, List<Member> memberBatch) {
        final Long clickCount = zSetOperations.score(KEY, nickname).longValue();
        Member exsistingMember = memberRepository.findByNickname(nickname);
        if (exsistingMember != null) {
            exsistingMember.updateClickCount(clickCount);
            memberBatch.add(exsistingMember);
            return;
        }
        final Member newMember = new Member(nickname, clickCount);
        memberBatch.add(newMember);
    }
}
