package clickme.clickme.svg.application;

import clickme.clickme.common.ErrorCode;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.Member;
import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.svg.application.exception.SvgException;
import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import clickme.clickme.svg.domain.document.SvgTransformer;
import clickme.clickme.svg.domain.document.strategy.DefaultSvgGenerationStrategy;
import clickme.clickme.svg.domain.document.strategy.GcsSvgGenerationStrategy;
import clickme.clickme.svg.domain.document.strategy.SvgGenerationStrategy;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SvgImageService {

    private static final String BUCKET_NAME = "clickme-test";

    private final RankingRepository rankingRepository;
    private final DailyClickRepository dailyClickRepository;
    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;
    private final MemberRepository memberRepository;
    private final Storage storage;
    private final SvgTransformer svgTransformer;

    public String generateClickableSvgImage(final String name, final String svgUrl) {
        try {
            final Count count = addAndGetCount(name);
            SvgGenerationStrategy strategy = chooseStrategy(svgUrl, name);
            Document doc = strategy.generateSvg(count, name, svgUrl);
            return svgTransformer.transform(doc);
        } catch (IOException | TransformerException e) {
            throw new SvgException("Error generating Clickable SVG Image", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private SvgGenerationStrategy chooseStrategy(String svgUrl, String name) {
        final String foundSvgImage = memberRepository.findSvgImageName(name)
                .orElse(null);
        if (svgUrl.equals("null") && foundSvgImage == null) {
            return new DefaultSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator);
        } else {
            return new GcsSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator, storage, BUCKET_NAME, memberRepository);
        }
    }

    private Count addAndGetCount(final String name) {
        final Count count = new Count(rankingRepository.findByName(name));
        if (count.isZero()) {
            rankingRepository.add(name);
            dailyClickRepository.add(name);
        }
        increaseCount(name);
        return count.increase();
    }

    private void increaseCount(final String name) {
        rankingRepository.increaseCount(name);
        dailyClickRepository.increaseCount(name);
    }

    @Transactional
    public String generateNonClickableSvgImage(final String name, final String svgUrl) {
        if (!svgUrl.equals("null")) {
            final Member member = memberRepository.findByName(name)
                    .orElse(new Member(0L, name, null));
            member.updateSvgUrl(svgUrl);
            memberRepository.save(member);
        }
        return generateClickableSvgImage(name, svgUrl);
    }
}
