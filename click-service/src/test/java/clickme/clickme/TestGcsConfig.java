package clickme.clickme;

import com.google.cloud.storage.Storage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestGcsConfig {

    @Bean
    @Primary
    public Storage storage() {
        return Mockito.mock(Storage.class);
    }
}
