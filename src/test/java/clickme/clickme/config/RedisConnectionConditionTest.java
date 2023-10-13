package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RedisConnectionConditionTest {

    @Mock
    private ConditionContext conditionContext;

    @Mock
    private AnnotatedTypeMetadata annotatedTypeMetadata;

    @Mock
    private Environment environment;

    @Mock
    private RedisConnectionCondition.RedisConnectionFactory connectionFactory;

    @Mock
    private RedisClient mockedClient;

    @Mock
    private StatefulRedisConnection<String, String> mockedConnection;

    @Mock
    private RedisCommands<String, String> syncCommands;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(conditionContext.getEnvironment()).thenReturn(environment);
        when(environment.getProperty("spring.data.redis.host")).thenReturn("localhost");
        when(environment.getProperty("spring.data.redis.port")).thenReturn("6379");

        when(connectionFactory.createClient(any())).thenReturn(mockedClient);
        when(connectionFactory.createConnection(any())).thenReturn(mockedConnection);

        when(mockedConnection.sync()).thenReturn(syncCommands);
        when(syncCommands.ping()).thenReturn("PONG");
    }

    @Test
    @DisplayName("redis 연결이 되었을 때 true를 반환한다..")
    void matchesSuccess() {
        RedisConnectionCondition condition = new RedisConnectionCondition(connectionFactory);

        boolean result = condition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("redis 연결이 되지 않을경우 false를 반환한다.")
    void matchesFail() {
        RedisConnectionCondition condition =
                new RedisConnectionCondition(new RedisConnectionCondition.RedisConnectionFactory());

        boolean result = condition.matches(conditionContext, annotatedTypeMetadata);

        assertThat(result).isFalse();
    }
}
