package clickme.clickme.acceptance;

import clickme.clickme.history.domain.ClickCountHistoryRepository;
import clickme.clickme.ranking.domain.DailyClickRepository;
import clickme.clickme.ranking.domain.MemberRepository;
import clickme.clickme.ranking.domain.RankingRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    private static final GenericContainer<?> REDIS;

    @Autowired
    protected DailyClickRepository dailyClickRepository;

    @Autowired
    protected ClickCountHistoryRepository clickCountHistoryRepository;

    @Autowired
    protected RankingRepository rankingRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    int port;


    static {
        REDIS = new GenericContainer<>("redis:latest")
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS.start();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> "" + REDIS.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.execute();
        clearRedis();
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
