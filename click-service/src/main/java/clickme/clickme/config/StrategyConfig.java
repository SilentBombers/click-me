package clickme.clickme.config;

import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import clickme.clickme.svg.domain.document.strategy.DefaultSvgGenerationStrategy;
import clickme.clickme.svg.domain.document.strategy.GcsSvgGenerationStrategy;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {

    @Bean
    public DefaultSvgGenerationStrategy defaultSvgGenerationStrategy(
            final SvgDocumentFactory svgDocumentFactory,
            final SvgDocumentManipulator svgDocumentManipulator
    ) {
        return new DefaultSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator);
    }

    @Bean
    public GcsSvgGenerationStrategy gcsSvgGenerationStrategy(
            final SvgDocumentFactory svgDocumentFactory,
            final SvgDocumentManipulator svgDocumentManipulator,
            final Storage storage,
            @Value("${gcs.storage.bucket-name}") final String bucketName,
            final MemberRepository memberRepository
    ) {
        return new GcsSvgGenerationStrategy(svgDocumentFactory, svgDocumentManipulator, storage, bucketName, memberRepository);
    }
}
