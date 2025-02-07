package clickme.clickme.upload.application;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class GcsService {

    private static final String PREFIX_PATH = "svgs/";
    private static final String FORMAT = ".svg";
    private static final String SVG_TYPE = "image/svg+xml";
    private static final String DEFAULT_STORAGE_URL = "https://storage.googleapis.com/%s/%s";

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public GcsService(final Storage storage) {
        this.storage = storage;
    }

    public String uploadSvg(String svgContent) {
        String fileName = PREFIX_PATH + UUID.randomUUID() + FORMAT;
        Bucket bucket = storage.get(bucketName);
        bucket.create(fileName, svgContent.getBytes(StandardCharsets.UTF_8), SVG_TYPE);
        return String.format(DEFAULT_STORAGE_URL, bucketName, fileName);
    }
}
