package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RedisConnectionCondition implements Condition {

    private final RedisConnectionFactory connectionFactory;

    public RedisConnectionCondition(final RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        RedisURI redisURI = RedisURI.builder()
                .withHost(context.getEnvironment().getProperty("spring.data.redis.host"))
                .withPort(Integer.parseInt(context.getEnvironment().getProperty("spring.data.redis.port")))
                .build();

        try (CloseableRedisClient closeableRedis = new CloseableRedisClient(redisURI, connectionFactory);
             StatefulRedisConnection<String, String> connection = closeableRedis.getConnection()
        ) {
            String result = connection.sync().ping();
            return "PONG" .equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    static class RedisConnectionFactory {

        public RedisClient createClient(RedisURI redisURI) {
            return RedisClient.create(redisURI);
        }

        public StatefulRedisConnection<String, String> createConnection(RedisClient client) {
            return client.connect();
        }
    }

    static class CloseableRedisClient implements AutoCloseable {

        private final RedisClient client;
        private final StatefulRedisConnection<String, String> connection;

        public CloseableRedisClient(RedisURI redisURI, RedisConnectionFactory factory) {
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
