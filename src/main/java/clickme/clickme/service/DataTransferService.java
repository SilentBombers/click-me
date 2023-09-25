package clickme.clickme.service;

import clickme.clickme.domain.Member;
import clickme.clickme.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataTransferService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    @Scheduled(fixedRate = 7200000)
    @Transactional
    public void transferData() {
        int start = 0;
        int batchSize = 1000;

        while (true) {
            Set<String> nicknameBatch = redisTemplate.opsForZSet().range("clicks", start, start + batchSize - 1);
            if (nicknameBatch.isEmpty()) {
                break;
            }

            List<Member> memberBatch = new ArrayList<>();
            for (String nickname : nicknameBatch) {
                Long clickCount = redisTemplate.opsForZSet().score("clicks", nickname).longValue();
                Member exsistingMember = memberRepository.findByNickname(nickname);
                if (exsistingMember != null) {
                    exsistingMember.updateClickCount(clickCount);
                    memberBatch.add(exsistingMember);
                } else {
                    Member newMember = new Member(nickname, clickCount);
                    memberBatch.add(newMember);
                }
            }

            memberRepository.saveAll(memberBatch);
            start += batchSize;
        }
    }
}
