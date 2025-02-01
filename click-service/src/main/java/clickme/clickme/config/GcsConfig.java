package clickme.clickme.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GcsConfig {

    @Value("${gcp.storage.credentials.location}")
    private String credentialsPath;

    @Bean
    public Storage storage(ResourceLoader resourceLoader) throws IOException {
        InputStream credentialsStream = resourceLoader.getResource(credentialsPath).getInputStream();
        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build()
                .getService();
    }
}
