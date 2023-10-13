package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RedisConnectionCondition implements Condition {

    private final RedisConnectionFactory connectionFactory;

    public RedisConnectionCondition() {
        this.connectionFactory = new RedisConnectionFactory();
    }

    public RedisConnectionCondition(final RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final RedisURI redisURI = RedisURI.builder()
                .withHost(context.getEnvironment().getProperty("spring.data.redis.host"))
                .withPort(Integer.parseInt(context.getEnvironment().getProperty("spring.data.redis.port")))
                .build();

        try (final CloseableRedisClient closeableRedis = new CloseableRedisClient(redisURI, connectionFactory);
             final StatefulRedisConnection<String, String> connection = closeableRedis.getConnection()
        ) {
            final String result = connection.sync().ping();
            return "PONG" .equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    static class CloseableRedisClient implements AutoCloseable {

        private final RedisClient client;
        private final StatefulRedisConnection<String, String> connection;

        public CloseableRedisClient(final RedisURI redisURI, final RedisConnectionFactory factory) {
            this.client = factory.createClient(redisURI);
            this.connection = factory.createConnection(client);
        }

        public RedisClient getClient() {
            return client;
        }

        public StatefulRedisConnection<String, String> getConnection() {
            return connection;
        }

        @Override
        public void close() {
            client.shutdown();
        }
    }
}
