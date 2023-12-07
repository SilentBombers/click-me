package clickme.clickme.ranking.application;

import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.ranking.ui.response.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final MemberRepository memberRepository;

    public Long findRankByName(final String name) {
        return rankingRepository.findRankByName(name);
    }

    public List<RankingResponse> findLiveRanking(final int start, final int end) {
        return rankingRepository.findLiveRanking(start, end)
                .stream()
                .map(ranking -> {
                    String profileImage = memberRepository.getProfileImageUrlByName(ranking.nickname());
                    return new RankingResponse(ranking.ranking(), ranking.nickname(), ranking.count(), profileImage);
                })
                .toList();
    }
}
