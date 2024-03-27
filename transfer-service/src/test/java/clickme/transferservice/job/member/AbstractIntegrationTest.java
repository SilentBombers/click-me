package clickme.transferservice.job.member;

import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.format.DateTimeFormatter;

@Sql("/init.sql")
@SpringBatchTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractIntegrationTest {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final MySQLContainer MY_SQL_CONTAINER;
    static final GenericContainer<?> REDIS;

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withUsername("username")
                .withPassword("password")
                .withDatabaseName("clickme");
        MY_SQL_CONTAINER.start();

        REDIS = new GenericContainer<>("redis:latest")
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS.start();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> "" + REDIS.getMappedPort(6379));
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }
}
