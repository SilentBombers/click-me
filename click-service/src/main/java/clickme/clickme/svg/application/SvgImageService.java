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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SvgImageService {

    private static final String BUCKET_NAME = "clickme-test2";

    private final RankingRepository rankingRepository;
    private final DailyClickRepository dailyClickRepository;
    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;
    private final MemberRepository memberRepository;
    private final Storage storage;
    private final SvgTransformer svgTransformer;

    public String generateClickableSvgImage(final String name) {
        try {
            final Count count = addAndGetCount(name);
            SvgGenerationStrategy strategy = chooseStrategy(name);
            Document doc = strategy.generateSvg(count, name);
            return svgTransformer.transform(doc);
        } catch (IOException | TransformerException e) {
            throw new SvgException("Error generating Clickable SVG Image", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private SvgGenerationStrategy chooseStrategy(String name) {
        final Optional<String> foundSvgImage = memberRepository.findSvgImageName(name);
        if (foundSvgImage.isPresent()) {
            return new GcsSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator, storage, BUCKET_NAME, memberRepository);
        } else {
            return new DefaultSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator);
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
        if (!svgUrl.equals("null") && !svgUrl.isEmpty()) {
            final Member member = memberRepository.getMemberByName(name);
            member.updateSvgUrl(svgUrl);
            memberRepository.save(member);
        }
        return generateNonClickableSvgImage(name);
    }

    private String generateNonClickableSvgImage(final String name) {
        try {
            final Count count = getClickCount(name);
            SvgGenerationStrategy strategy = chooseStrategy(name);
            Document doc = strategy.generateSvg(count, name);
            return svgTransformer.transform(doc);
        } catch (IOException | TransformerException e) {
            throw new SvgException("Error generating Clickable SVG Image", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Count getClickCount(final String name) {
        return new Count(rankingRepository.findByName(name));
    }
}
