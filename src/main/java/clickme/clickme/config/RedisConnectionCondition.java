package clickme.clickme.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RedisConnectionCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        RedisURI redisURI = RedisURI.builder()
                .withHost(context.getEnvironment().getProperty("spring.data.redis.host"))
                .withPort(Integer.parseInt(context.getEnvironment().getProperty("spring.data.redis.port")))
                .build();

        try (CloseableRedisClient closeableRedis = new CloseableRedisClient(redisURI);
             StatefulRedisConnection<String, String> connection = closeableRedis.getClient().connect()
        ) {
            String result = connection.sync().ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    static class CloseableRedisClient implements AutoCloseable {

        private final RedisClient client;

        public CloseableRedisClient(RedisURI redisURI) {
            this.client = RedisClient.create(redisURI);
        }

        public RedisClient getClient() {
            return client;
        }

        @Override
        public void close() {
            client.shutdown();
        }
    }
}
