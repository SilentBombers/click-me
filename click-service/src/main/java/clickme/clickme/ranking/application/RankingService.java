package clickme.clickme.ranking.application;

import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.ranking.infrastructure.dto.RankingDto;
import clickme.clickme.ranking.ui.response.RankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final MemberRepository memberRepository;

    public Long findRankByName(final String name) {
        log.info("Finding rank for name: {}", name);
        return rankingRepository.findRankByName(name);
    }

    public List<RankingResponse> findLiveRanking(final int start, final int end) {
        log.info("Finding live ranking from {} to {}", start, end);
        return rankingRepository.findLiveRanking(start, end)
                .stream()
                .map(this::toRankingResponse)
                .toList();
    }

    private RankingResponse toRankingResponse(RankingDto ranking) {
        String profileImage = memberRepository.getProfileImageUrlByName(ranking.nickname());
        return new RankingResponse(ranking.ranking(), ranking.nickname(), ranking.count(), profileImage);
    }
}
