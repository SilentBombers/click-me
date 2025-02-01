package clickme.clickme.upload.application;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GcsService {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public GcsService(final Storage storage) {
        this.storage = storage;
    }

    public String uploadSvg(String svgContent) {
        String fileName = "svgs/" + UUID.randomUUID() + ".svg";
        Bucket bucket = storage.get(bucketName);
        bucket.create(fileName, svgContent.getBytes(), "image/svg+xml");

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
