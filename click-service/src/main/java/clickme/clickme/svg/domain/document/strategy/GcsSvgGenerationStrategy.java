package clickme.clickme.svg.domain.document.strategy;

import clickme.clickme.ranking.domain.Member;
import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.svg.domain.count.Count;
import clickme.clickme.svg.domain.document.SvgDocumentFactory;
import clickme.clickme.svg.domain.document.SvgDocumentManipulator;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GcsSvgGenerationStrategy implements SvgGenerationStrategy {

    private final SvgDocumentFactory svgDocumentFactory;
    private final SvgDocumentManipulator svgDocumentManipulator;
    private final Storage storage;
    private final String bucketName;
    private final MemberRepository memberRepository;

    public GcsSvgGenerationStrategy(SvgDocumentFactory svgDocumentFactory,
                                    SvgDocumentManipulator svgDocumentManipulator,
                                    Storage storage,
                                    String bucketName,
                                    MemberRepository memberRepository) {
        this.svgDocumentFactory = svgDocumentFactory;
        this.svgDocumentManipulator = svgDocumentManipulator;
        this.storage = storage;
        this.bucketName = bucketName;
        this.memberRepository = memberRepository;
    }

    @Override
    public Document generateSvg(Count count, String name, String svgUrl) throws IOException {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new IOException("Member not found"));
        String memberSvgUrl = member.getSvgUrl();
        Document doc = loadSvgFromGcs(memberSvgUrl);
        doc = svgDocumentManipulator.drawText(doc, count);
        return doc;
    }

    private Document loadSvgFromGcs(String url) throws IOException {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        Blob blob = storage.get(bucketName, "svgs/" + fileName);
        if (blob == null) {
            throw new IOException("SVG file not found: " + fileName);
        }
        String svgContent = new String(blob.getContent(), StandardCharsets.UTF_8);
        return svgDocumentFactory.createSvgDocumentFromString(svgContent);
    }
}
